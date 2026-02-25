using MediatR;
using MyApp.CoreService.Data;
using MyApp.CoreService.DTOs.Responses;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Models;

namespace MyApp.CoreService.Features.Accounts.Commands.CreateAccount;

public class CreateAccountHandler : IRequestHandler<CreateAccountCommand, AccountResponse>
{
    private readonly CoreDbContext _db;

    public CreateAccountHandler(CoreDbContext db) => _db = db;

    public async Task<AccountResponse> Handle(CreateAccountCommand cmd, CancellationToken ct)
    {
        var currency = cmd.Currency.Trim().ToUpper();
        if (string.IsNullOrEmpty(currency) || currency.Length > 3)
            throw new ArgumentException("Currency must be 1-3 characters.");

        var account = new Account
        {
            OwnerId = cmd.OwnerId,
            Currency = currency,
            Balance = 0m,
            Status = AccountStatus.Open,
            CreatedAt = DateTimeOffset.UtcNow
        };

        _db.Accounts.Add(account);
        await _db.SaveChangesAsync(ct);

        return Map(account);
    }

    internal static AccountResponse Map(Account a) =>
        new(a.Id, a.OwnerId, a.Balance, a.Currency, a.Status, a.CreatedAt, a.ClosedAt);
}
