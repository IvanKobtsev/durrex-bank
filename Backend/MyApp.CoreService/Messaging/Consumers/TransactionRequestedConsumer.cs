using MassTransit;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using MyApp.CoreService.Data;
using MyApp.CoreService.DTOs.Responses;
using MyApp.CoreService.Enums;
using MyApp.CoreService.ExchangeRates;
using MyApp.CoreService.Features.Transactions.Shared;
using MyApp.CoreService.Hubs;
using MyApp.CoreService.Messaging.Messages;
using MyApp.CoreService.Models;
using MyApp.CoreService.Services;

namespace MyApp.CoreService.Messaging.Consumers;

public class TransactionRequestedConsumer(
    CoreDbContext db,
    IHubContext<TransactionHub> hub,
    IExchangeRateService exchangeRates,
    IFirebaseNotificationService firebaseNotificationService
) : IConsumer<TransactionRequested>
{
    public async Task Consume(ConsumeContext<TransactionRequested> context)
    {
        var msg = context.Message;
        var ct = context.CancellationToken;

        var result = msg.Type switch
        {
            TransactionType.Deposit => await HandleDeposit(msg, ct),
            TransactionType.Withdrawal => await HandleWithdraw(msg, ct),
            TransactionType.Transfer => await HandleTransfer(msg, ct),
            TransactionType.CreditRepayment => await HandleTransfer(msg, ct),
            TransactionType.Debit => await HandleDebit(msg, ct),
            _ => throw new ArgumentException($"Unknown transaction type {msg.Type}"),
        };

        await hub
            .Clients.Group($"account-{result.AccountId}")
            .SendAsync("NewTransaction", result, ct);

        if (result.RelatedAccountId.HasValue)
        {
            await hub
                .Clients.Group($"account-{result.RelatedAccountId.Value}")
                .SendAsync("NewTransaction", result, ct);
        }

        await SendFirebaseNotificationsAsync(result, ct);

        await context.RespondAsync(new TransactionCompleted(msg.MessageId, result));
    }

    private async Task SendFirebaseNotificationsAsync(
        TransactionResponse transaction,
        CancellationToken ct
    )
    {
        var account = await db.Accounts.FindAsync(
            new object[] { transaction.AccountId },
            cancellationToken: ct
        );
        if (account is null)
            return;

        var title = $"Транзакция: {transaction.Type}";
        var body =
            $"Сумма: {transaction.Amount} {account.Currency}. Доступно: {transaction.BalanceAfter} {account.Currency}";

        var sourceNotificationData = CreateTransactionNotificationData(transaction, account.Id, account.Currency);

        await firebaseNotificationService.SendToUserAsync(
            account.OwnerId,
            title,
            body,
            sourceNotificationData,
            ct
        );

        await firebaseNotificationService.SendToAllEmployeesAsync(
            title,
            body,
            sourceNotificationData,
            ct
        );

        if (transaction.RelatedAccountId.HasValue)
        {
            var relatedAccount = await db.Accounts.FindAsync(
                new object[] { transaction.RelatedAccountId.Value },
                cancellationToken: ct
            );
            if (relatedAccount is not null)
            {
                var relatedNotificationData = CreateTransactionNotificationData(
                    transaction,
                    relatedAccount.Id,
                    relatedAccount.Currency
                );

                await firebaseNotificationService.SendToUserAsync(
                    relatedAccount.OwnerId,
                    title,
                    body,
                    relatedNotificationData,
                    ct
                );
            }
        }
    }

    private static Dictionary<string, string> CreateTransactionNotificationData(
        TransactionResponse transaction,
        int accountId,
        string currency
    )
    {
        var data = new Dictionary<string, string>
        {
            ["transactionType"] = transaction.Type.ToString(),
            ["accountId"] = accountId.ToString(),
            ["amount"] = transaction.Amount.ToString("F2"),
            ["currency"] = currency,
            ["balanceAfter"] = transaction.BalanceAfter.ToString("F2"),
            ["screen"] = "transactions",
            ["route"] = $"/account/{accountId}",
        };

        if (transaction.RelatedAccountId.HasValue)
        {
            data["relatedAccountId"] = transaction.RelatedAccountId.Value.ToString();
        }

        return data;
    }

    private async Task<TransactionResponse> HandleDeposit(
        TransactionRequested msg,
        CancellationToken ct
    )
    {
        if (msg.Amount <= 0)
            throw new ArgumentException("Amount must be positive.");

        var account = await AccountHelper.GetOpenAccountAsync(db, msg.AccountId, ct);

        var tx = AccountHelper.CreateTransaction(
            account,
            TransactionType.Deposit,
            msg.Amount,
            msg.Description
        );
        account.Balance += msg.Amount;
        tx.BalanceAfter = account.Balance;

        db.Transactions.Add(tx);
        await db.SaveChangesAsync(ct);

        return AccountHelper.MapTransaction(tx);
    }

    private async Task<TransactionResponse> HandleWithdraw(
        TransactionRequested msg,
        CancellationToken ct
    )
    {
        if (msg.Amount <= 0)
            throw new ArgumentException("Amount must be positive.");

        var account = await AccountHelper.GetOpenAccountAsync(db, msg.AccountId, ct);

        if (account.Balance < msg.Amount)
            throw new InvalidOperationException("Insufficient funds.");

        var tx = AccountHelper.CreateTransaction(
            account,
            TransactionType.Withdrawal,
            msg.Amount,
            msg.Description
        );
        account.Balance -= msg.Amount;
        tx.BalanceAfter = account.Balance;

        db.Transactions.Add(tx);
        await db.SaveChangesAsync(ct);

        return AccountHelper.MapTransaction(tx);
    }

    private async Task<TransactionResponse> HandleDebit(
        TransactionRequested msg,
        CancellationToken ct
    )
    {
        if (msg.Amount <= 0)
            throw new ArgumentException("Amount must be positive.");

        var account = await AccountHelper.GetOpenAccountAsync(db, msg.AccountId, ct);

        if (account.Balance < msg.Amount)
            throw new InvalidOperationException("Insufficient funds.");

        var tx = AccountHelper.CreateTransaction(
            account,
            TransactionType.Debit,
            msg.Amount,
            msg.Description
        );
        account.Balance -= msg.Amount;
        tx.BalanceAfter = account.Balance;

        db.Transactions.Add(tx);
        await db.SaveChangesAsync(ct);

        return AccountHelper.MapTransaction(tx);
    }

    private async Task<TransactionResponse> HandleTransfer(
        TransactionRequested msg,
        CancellationToken ct
    )
    {
        if (msg.Amount <= 0)
            throw new ArgumentException("Amount must be positive.");

        if (!msg.RelatedAccountId.HasValue)
            throw new ArgumentException("Transfer requires RelatedAccountId.");

        if (msg.AccountId == msg.RelatedAccountId.Value)
            throw new ArgumentException("Cannot transfer to the same account.");

        var ids = new[] { msg.AccountId, msg.RelatedAccountId.Value }.Order().ToArray();
        var accounts = await db.Accounts.Where(a => ids.Contains(a.Id)).ToListAsync(ct);

        var source =
            accounts.FirstOrDefault(a => a.Id == msg.AccountId)
            ?? throw new KeyNotFoundException($"Source account {msg.AccountId} not found.");
        var target =
            accounts.FirstOrDefault(a => a.Id == msg.RelatedAccountId.Value)
            ?? throw new KeyNotFoundException($"Target account {msg.RelatedAccountId} not found.");

        if (source.Status != AccountStatus.Open)
            throw new InvalidOperationException($"Source account {msg.AccountId} is not open.");
        if (target.Status != AccountStatus.Open)
            throw new InvalidOperationException(
                $"Target account {msg.RelatedAccountId} is not open."
            );
        if (source.Balance < msg.Amount)
            throw new InvalidOperationException("Insufficient funds.");

        decimal targetCreditAmount;
        Transaction sourceTx;
        Transaction targetTx;

        if (string.Equals(source.Currency, target.Currency, StringComparison.OrdinalIgnoreCase))
        {
            targetCreditAmount = msg.Amount;
            sourceTx = AccountHelper.CreateTransaction(
                source,
                msg.Type,
                msg.Amount,
                msg.Description
            );
            targetTx = AccountHelper.CreateTransaction(
                target,
                msg.Type,
                msg.Amount,
                msg.Description
            );
        }
        else
        {
            var rate = await exchangeRates.GetRateAsync(source.Currency, target.Currency, ct);
            targetCreditAmount = Math.Round(msg.Amount * rate, 2, MidpointRounding.AwayFromZero);

            sourceTx = AccountHelper.CreateTransaction(
                source,
                msg.Type,
                msg.Amount,
                msg.Description
            );
            sourceTx.ExchangeRate = rate;
            sourceTx.SourceCurrency = source.Currency;

            targetTx = AccountHelper.CreateTransaction(
                target,
                msg.Type,
                targetCreditAmount,
                msg.Description
            );
        }

        sourceTx.RelatedAccountId = target.Id;
        source.Balance -= msg.Amount;
        sourceTx.BalanceAfter = source.Balance;

        targetTx.RelatedAccountId = source.Id;
        target.Balance += targetCreditAmount;
        targetTx.BalanceAfter = target.Balance;

        db.Transactions.AddRange(sourceTx, targetTx);
        await db.SaveChangesAsync(ct);

        return AccountHelper.MapTransaction(sourceTx);
    }
}
