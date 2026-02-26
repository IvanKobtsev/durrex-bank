using MediatR;
using MyApp.CoreService.Data;
using MyApp.CoreService.DTOs.Responses;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Features.Transactions.Shared;

namespace MyApp.CoreService.Features.Transactions.Commands.Withdraw;

public class WithdrawHandler : IRequestHandler<WithdrawCommand, TransactionResponse>
{
    private readonly CoreDbContext _db;

    public WithdrawHandler(CoreDbContext db) => _db = db;

    public async Task<TransactionResponse> Handle(WithdrawCommand cmd, CancellationToken ct)
    {
        if (cmd.Amount <= 0)
            throw new ArgumentException("Amount must be positive.");

        var account = await AccountHelper.GetOpenAccountAsync(_db, cmd.AccountId, ct);

        if (account.Balance < cmd.Amount)
            throw new InvalidOperationException("Insufficient funds.");

        var tx = AccountHelper.CreateTransaction(account, TransactionType.Withdrawal, cmd.Amount, cmd.Description);
        account.Balance -= cmd.Amount;
        tx.BalanceAfter = account.Balance;

        _db.Transactions.Add(tx);
        await _db.SaveChangesAsync(ct);

        return AccountHelper.MapTransaction(tx);
    }
}
