using MyApp.CoreService.Data;
using MyApp.CoreService.DTOs.Responses;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Models;

namespace MyApp.CoreService.Features.Transactions.Shared;

internal static class AccountHelper
{
    internal static async Task<Account> GetOpenAccountAsync(CoreDbContext db, int accountId, CancellationToken ct)
    {
        var account = await db.Accounts.FindAsync([accountId], ct)
            ?? throw new KeyNotFoundException($"Account {accountId} not found.");

        if (account.Status != AccountStatus.Open)
            throw new InvalidOperationException($"Account {accountId} is not open.");

        return account;
    }

    internal static Transaction CreateTransaction(
        Account account,
        TransactionType type,
        decimal amount,
        string? description) => new()
    {
        AccountId = account.Id,
        Type = type,
        Amount = amount,
        BalanceBefore = account.Balance,
        BalanceAfter = 0m,
        Description = description,
        CreatedAt = DateTimeOffset.UtcNow
    };

    internal static TransactionResponse MapTransaction(Transaction t) => new(
        t.Id, t.AccountId, t.Type, t.Amount,
        t.BalanceBefore, t.BalanceAfter,
        t.RelatedAccountId, t.Description, t.CreatedAt);
}
