using FluentAssertions;
using MyApp.CoreService.Data;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Features.Accounts.Commands.CloseAccount;
using MyApp.CoreService.Models;
using MyApp.CoreService.Tests.Unit.Helpers;

namespace MyApp.CoreService.Tests.Unit.Handlers.Accounts.Commands;

public class CloseAccountHandlerTests
{
    private static CloseAccountHandler Sut(CoreDbContext db) => new(db);

    private static async Task<Account> SeedAccountAsync(
        CoreDbContext db,
        decimal balance = 0m,
        AccountStatus status = AccountStatus.Open)
    {
        var account = new Account
        {
            OwnerId = 1,
            Balance = balance,
            Currency = "RUB",
            Status = status,
            CreatedAt = DateTimeOffset.UtcNow
        };
        db.Accounts.Add(account);
        await db.SaveChangesAsync();
        return account;
    }

    [Fact]
    public async Task Handle_OpenAccountWithZeroBalance_ClosesAccount()
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedAccountAsync(db);

        var result = await Sut(db).Handle(new CloseAccountCommand(account.Id), default);

        result.Status.Should().Be(AccountStatus.Closed);
        result.ClosedAt.Should().NotBeNull();
    }

    [Fact]
    public async Task Handle_NonExistentAccount_ThrowsKeyNotFoundException()
    {
        await using var db = DbContextFactory.Create();

        await Sut(db).Invoking(h => h.Handle(new CloseAccountCommand(999), default))
            .Should().ThrowAsync<KeyNotFoundException>()
            .WithMessage("*999*");
    }

    [Fact]
    public async Task Handle_AlreadyClosedAccount_ThrowsInvalidOperationException()
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedAccountAsync(db, status: AccountStatus.Closed);

        await Sut(db).Invoking(h => h.Handle(new CloseAccountCommand(account.Id), default))
            .Should().ThrowAsync<InvalidOperationException>()
            .WithMessage("Account is already closed.");
    }

    [Fact]
    public async Task Handle_AccountWithPositiveBalance_ThrowsInvalidOperationException()
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedAccountAsync(db, balance: 100m);

        await Sut(db).Invoking(h => h.Handle(new CloseAccountCommand(account.Id), default))
            .Should().ThrowAsync<InvalidOperationException>()
            .WithMessage("Cannot close account with positive balance.");
    }
}
