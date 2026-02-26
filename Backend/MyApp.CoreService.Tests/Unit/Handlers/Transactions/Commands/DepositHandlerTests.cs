using FluentAssertions;
using MyApp.CoreService.Data;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Features.Transactions.Commands.Deposit;
using MyApp.CoreService.Models;
using MyApp.CoreService.Tests.Unit.Helpers;

namespace MyApp.CoreService.Tests.Unit.Handlers.Transactions.Commands;

public class DepositHandlerTests
{
    private static DepositHandler Sut(CoreDbContext db) => new(db);

    private static async Task<Account> SeedOpenAccountAsync(CoreDbContext db, decimal balance = 0m)
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
    public async Task Handle_ValidAmount_IncreasesAccountBalance()
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedOpenAccountAsync(db, balance: 100m);

        await Sut(db).Handle(new DepositCommand(account.Id, Amount: 50m, Description: null), default);

        db.Accounts.Find(account.Id)!.Balance.Should().Be(150m);
    }

    [Fact]
    public async Task Handle_ValidAmount_ReturnsTransactionWithCorrectBalances()
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedOpenAccountAsync(db, balance: 200m);

        var tx = await Sut(db).Handle(new DepositCommand(account.Id, Amount: 75m, Description: "Test deposit"), default);

        tx.Type.Should().Be(TransactionType.Deposit);
        tx.Amount.Should().Be(75m);
        tx.BalanceBefore.Should().Be(200m);
        tx.BalanceAfter.Should().Be(275m);
        tx.Description.Should().Be("Test deposit");
    }

    [Fact]
    public async Task Handle_ValidAmount_PersistsTransactionToDatabase()
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedOpenAccountAsync(db);

        await Sut(db).Handle(new DepositCommand(account.Id, Amount: 10m, Description: null), default);

        db.Transactions.Should().ContainSingle(t => t.AccountId == account.Id && t.Type == TransactionType.Deposit);
    }

    [Theory]
    [InlineData(0)]
    [InlineData(-1)]
    [InlineData(-100)]
    public async Task Handle_NonPositiveAmount_ThrowsArgumentException(decimal amount)
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedOpenAccountAsync(db);

        await Sut(db).Invoking(h => h.Handle(new DepositCommand(account.Id, amount, null), default))
            .Should().ThrowAsync<ArgumentException>()
            .WithMessage("Amount must be positive.");
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

        await Sut(db).Invoking(h => h.Handle(new DepositCommand(account.Id, 50m, null), default))
            .Should().ThrowAsync<InvalidOperationException>()
            .WithMessage($"*{account.Id}*not open*");
    }

    [Fact]
    public async Task Handle_NonExistentAccount_ThrowsKeyNotFoundException()
    {
        await using var db = DbContextFactory.Create();

        await Sut(db).Invoking(h => h.Handle(new DepositCommand(AccountId: 999, Amount: 10m, Description: null), default))
            .Should().ThrowAsync<KeyNotFoundException>()
            .WithMessage("*999*");
    }
}
