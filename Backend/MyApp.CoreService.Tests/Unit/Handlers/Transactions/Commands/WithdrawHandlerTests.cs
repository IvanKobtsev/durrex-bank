using FluentAssertions;
using MyApp.CoreService.Data;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Features.Transactions.Commands.Withdraw;
using MyApp.CoreService.Models;
using MyApp.CoreService.Tests.Unit.Helpers;

namespace MyApp.CoreService.Tests.Unit.Handlers.Transactions.Commands;

public class WithdrawHandlerTests
{
    private static WithdrawHandler Sut(CoreDbContext db) => new(db);

    private static async Task<Account> SeedOpenAccountAsync(CoreDbContext db, decimal balance = 500m)
    {
        var account = new Account
        {
            OwnerId = 1,
            Balance = balance,
            Currency = "RUB",
            Status = AccountStatus.Open,
            CreatedAt = DateTimeOffset.UtcNow
        };
        db.Accounts.Add(account);
        await db.SaveChangesAsync();
        return account;
    }

    [Fact]
    public async Task Handle_ValidAmount_DecreasesAccountBalance()
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedOpenAccountAsync(db, balance: 300m);

        await Sut(db).Handle(new WithdrawCommand(account.Id, Amount: 100m, Description: null), default);

        db.Accounts.Find(account.Id)!.Balance.Should().Be(200m);
    }

    [Fact]
    public async Task Handle_ValidAmount_ReturnsTransactionWithCorrectBalances()
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedOpenAccountAsync(db, balance: 500m);

        var tx = await Sut(db).Handle(new WithdrawCommand(account.Id, Amount: 150m, Description: "Rent"), default);

        tx.Type.Should().Be(TransactionType.Withdrawal);
        tx.Amount.Should().Be(150m);
        tx.BalanceBefore.Should().Be(500m);
        tx.BalanceAfter.Should().Be(350m);
        tx.Description.Should().Be("Rent");
    }

    [Theory]
    [InlineData(0)]
    [InlineData(-50)]
    public async Task Handle_NonPositiveAmount_ThrowsArgumentException(decimal amount)
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedOpenAccountAsync(db);

        await Sut(db).Invoking(h => h.Handle(new WithdrawCommand(account.Id, amount, null), default))
            .Should().ThrowAsync<ArgumentException>()
            .WithMessage("Amount must be positive.");
    }

    [Fact]
    public async Task Handle_InsufficientFunds_ThrowsInvalidOperationException()
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedOpenAccountAsync(db, balance: 50m);

        await Sut(db).Invoking(h => h.Handle(new WithdrawCommand(account.Id, Amount: 100m, Description: null), default))
            .Should().ThrowAsync<InvalidOperationException>()
            .WithMessage("Insufficient funds.");
    }

    [Fact]
    public async Task Handle_ClosedAccount_ThrowsInvalidOperationException()
    {
        await using var db = DbContextFactory.Create();
        var account = new Account
        {
            OwnerId = 1, Currency = "RUB",
            Status = AccountStatus.Closed,
            CreatedAt = DateTimeOffset.UtcNow
        };
        db.Accounts.Add(account);
        await db.SaveChangesAsync();

        await Sut(db).Invoking(h => h.Handle(new WithdrawCommand(account.Id, 50m, null), default))
            .Should().ThrowAsync<InvalidOperationException>()
            .WithMessage($"*{account.Id}*not open*");
    }

    [Fact]
    public async Task Handle_NonExistentAccount_ThrowsKeyNotFoundException()
    {
        await using var db = DbContextFactory.Create();

        await Sut(db).Invoking(h => h.Handle(new WithdrawCommand(AccountId: 999, Amount: 10m, Description: null), default))
            .Should().ThrowAsync<KeyNotFoundException>()
            .WithMessage("*999*");
    }
}
