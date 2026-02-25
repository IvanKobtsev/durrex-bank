using MediatR;
using MyApp.CoreService.Data;
using MyApp.CoreService.DTOs.Responses;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Features.Transactions.Shared;

namespace MyApp.CoreService.Features.Transactions.Commands.Deposit;

public class DepositHandler : IRequestHandler<DepositCommand, TransactionResponse>
{
    private readonly CoreDbContext _db;

    public DepositHandler(CoreDbContext db) => _db = db;

    public async Task<TransactionResponse> Handle(DepositCommand cmd, CancellationToken ct)
    {
        if (cmd.Amount <= 0)
            throw new ArgumentException("Amount must be positive.");

        var account = await AccountHelper.GetOpenAccountAsync(_db, cmd.AccountId, ct);

        var tx = AccountHelper.CreateTransaction(account, TransactionType.Deposit, cmd.Amount, cmd.Description);
        account.Balance += cmd.Amount;
        tx.BalanceAfter = account.Balance;

        _db.Transactions.Add(tx);
        await _db.SaveChangesAsync(ct);

        return AccountHelper.MapTransaction(tx);
    }
}
