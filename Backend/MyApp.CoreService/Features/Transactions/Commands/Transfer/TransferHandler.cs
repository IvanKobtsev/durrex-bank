using MediatR;
using Microsoft.EntityFrameworkCore;
using MyApp.CoreService.Data;
using MyApp.CoreService.DTOs.Responses;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Features.Transactions.Shared;

namespace MyApp.CoreService.Features.Transactions.Commands.Transfer;

public class TransferHandler : IRequestHandler<TransferCommand, (TransactionResponse Source, TransactionResponse Target)>
{
    private readonly CoreDbContext _db;

    public TransferHandler(CoreDbContext db) => _db = db;

    public async Task<(TransactionResponse Source, TransactionResponse Target)> Handle(
        TransferCommand cmd, CancellationToken ct)
    {
        if (cmd.Amount <= 0)
            throw new ArgumentException("Amount must be positive.");

        if (cmd.SourceAccountId == cmd.TargetAccountId)
            throw new ArgumentException("Cannot transfer to the same account.");

        // Load both accounts in a single query, ordered by ID to prevent potential deadlocks
        var ids = new[] { cmd.SourceAccountId, cmd.TargetAccountId }.Order().ToArray();
        var accounts = await _db.Accounts
            .Where(a => ids.Contains(a.Id))
            .ToListAsync(ct);

        var source = accounts.FirstOrDefault(a => a.Id == cmd.SourceAccountId)
            ?? throw new KeyNotFoundException($"Source account {cmd.SourceAccountId} not found.");
        var target = accounts.FirstOrDefault(a => a.Id == cmd.TargetAccountId)
            ?? throw new KeyNotFoundException($"Target account {cmd.TargetAccountId} not found.");

        if (source.Status != AccountStatus.Open)
            throw new InvalidOperationException($"Source account {cmd.SourceAccountId} is not open.");
        if (target.Status != AccountStatus.Open)
            throw new InvalidOperationException($"Target account {cmd.TargetAccountId} is not open.");
        if (source.Balance < cmd.Amount)
            throw new InvalidOperationException("Insufficient funds.");

        var sourceTx = AccountHelper.CreateTransaction(source, TransactionType.Transfer, cmd.Amount, cmd.Description);
        sourceTx.RelatedAccountId = target.Id;
        source.Balance -= cmd.Amount;
        sourceTx.BalanceAfter = source.Balance;

        var targetTx = AccountHelper.CreateTransaction(target, TransactionType.Transfer, cmd.Amount, cmd.Description);
        targetTx.RelatedAccountId = source.Id;
        target.Balance += cmd.Amount;
        targetTx.BalanceAfter = target.Balance;

        _db.Transactions.AddRange(sourceTx, targetTx);
        await _db.SaveChangesAsync(ct); // single SaveChanges = atomic

        return (AccountHelper.MapTransaction(sourceTx), AccountHelper.MapTransaction(targetTx));
    }
}
