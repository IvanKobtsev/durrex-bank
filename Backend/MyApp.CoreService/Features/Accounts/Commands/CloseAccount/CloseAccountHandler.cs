using MediatR;
using MyApp.CoreService.Data;
using MyApp.CoreService.DTOs.Responses;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Features.Accounts.Commands.CreateAccount;

namespace MyApp.CoreService.Features.Accounts.Commands.CloseAccount;

public class CloseAccountHandler : IRequestHandler<CloseAccountCommand, AccountResponse>
{
    private readonly CoreDbContext _db;

    public CloseAccountHandler(CoreDbContext db) => _db = db;

    public async Task<AccountResponse> Handle(CloseAccountCommand cmd, CancellationToken ct)
    {
        var account = await _db.Accounts.FindAsync([cmd.AccountId], ct)
            ?? throw new KeyNotFoundException($"Account {cmd.AccountId} not found.");

        if (account.Status == AccountStatus.Closed)
            throw new InvalidOperationException("Account is already closed.");

        if (account.Balance > 0m)
            throw new InvalidOperationException("Cannot close account with positive balance.");

        account.Status = AccountStatus.Closed;
        account.ClosedAt = DateTimeOffset.UtcNow;
        await _db.SaveChangesAsync(ct);

        return CreateAccountHandler.Map(account);
    }
}
