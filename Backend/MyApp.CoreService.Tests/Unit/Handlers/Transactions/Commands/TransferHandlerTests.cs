using FluentAssertions;
using MyApp.CoreService.Data;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Features.Transactions.Commands.Transfer;
using MyApp.CoreService.Models;
using MyApp.CoreService.Tests.Unit.Helpers;

namespace MyApp.CoreService.Tests.Unit.Handlers.Transactions.Commands;

public class TransferHandlerTests
{
    private static TransferHandler Sut(CoreDbContext db) => new(db);

    private static async Task<Account> SeedAccountAsync(
        CoreDbContext db,
        decimal balance = 500m,
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
    public async Task Handle_ValidTransfer_UpdatesBothBalances()
    {
        await using var db = DbContextFactory.Create();
        var source = await SeedAccountAsync(db, balance: 400m);
        var target = await SeedAccountAsync(db, balance: 100m);

        await Sut(db).Handle(new TransferCommand(source.Id, target.Id, Amount: 150m, Description: null), default);

        db.Accounts.Find(source.Id)!.Balance.Should().Be(250m);
        db.Accounts.Find(target.Id)!.Balance.Should().Be(250m);
    }

    [Fact]
    public async Task Handle_ValidTransfer_CreatesTwoLinkedTransactions()
    {
        await using var db = DbContextFactory.Create();
        var source = await SeedAccountAsync(db, balance: 300m);
        var target = await SeedAccountAsync(db, balance: 0m);

        var (sourceTx, targetTx) = await Sut(db).Handle(
            new TransferCommand(source.Id, target.Id, Amount: 100m, Description: "Gift"), default);

        sourceTx.Type.Should().Be(TransactionType.Transfer);
        sourceTx.Amount.Should().Be(100m);
        sourceTx.RelatedAccountId.Should().Be(target.Id);
        sourceTx.BalanceBefore.Should().Be(300m);
        sourceTx.BalanceAfter.Should().Be(200m);

        targetTx.Type.Should().Be(TransactionType.Transfer);
        targetTx.Amount.Should().Be(100m);
        targetTx.RelatedAccountId.Should().Be(source.Id);
        targetTx.BalanceBefore.Should().Be(0m);
        targetTx.BalanceAfter.Should().Be(100m);

        db.Transactions.Should().HaveCount(2);
    }

    [Theory]
    [InlineData(0)]
    [InlineData(-10)]
    public async Task Handle_NonPositiveAmount_ThrowsArgumentException(decimal amount)
    {
        await using var db = DbContextFactory.Create();
        var source = await SeedAccountAsync(db);
        var target = await SeedAccountAsync(db);

        await Sut(db).Invoking(h => h.Handle(new TransferCommand(source.Id, target.Id, amount, null), default))
            .Should().ThrowAsync<ArgumentException>()
            .WithMessage("Amount must be positive.");
    }

    [Fact]
    public async Task Handle_SameSourceAndTarget_ThrowsArgumentException()
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedAccountAsync(db);

        await Sut(db).Invoking(h => h.Handle(new TransferCommand(account.Id, account.Id, 10m, null), default))
            .Should().ThrowAsync<ArgumentException>()
            .WithMessage("*same account*");
    }

    [Fact]
    public async Task Handle_SourceAccountNotFound_ThrowsKeyNotFoundException()
    {
        await using var db = DbContextFactory.Create();
        var target = await SeedAccountAsync(db);

        await Sut(db).Invoking(h => h.Handle(new TransferCommand(SourceAccountId: 999, target.Id, 10m, null), default))
            .Should().ThrowAsync<KeyNotFoundException>()
            .WithMessage("*999*");
    }

    [Fact]
    public async Task Handle_TargetAccountNotFound_ThrowsKeyNotFoundException()
    {
        await using var db = DbContextFactory.Create();
        var source = await SeedAccountAsync(db, balance: 100m);

        await Sut(db).Invoking(h => h.Handle(new TransferCommand(source.Id, TargetAccountId: 999, 10m, null), default))
            .Should().ThrowAsync<KeyNotFoundException>()
            .WithMessage("*999*");
    }

    [Fact]
    public async Task Handle_ClosedSourceAccount_ThrowsInvalidOperationException()
    {
        await using var db = DbContextFactory.Create();
        var source = await SeedAccountAsync(db, status: AccountStatus.Closed);
        var target = await SeedAccountAsync(db);

        await Sut(db).Invoking(h => h.Handle(new TransferCommand(source.Id, target.Id, 10m, null), default))
            .Should().ThrowAsync<InvalidOperationException>()
            .WithMessage($"*{source.Id}*not open*");
    }

    [Fact]
    public async Task Handle_ClosedTargetAccount_ThrowsInvalidOperationException()
    {
        await using var db = DbContextFactory.Create();
        var source = await SeedAccountAsync(db, balance: 100m);
        var target = await SeedAccountAsync(db, status: AccountStatus.Closed);

        await Sut(db).Invoking(h => h.Handle(new TransferCommand(source.Id, target.Id, 10m, null), default))
            .Should().ThrowAsync<InvalidOperationException>()
            .WithMessage($"*{target.Id}*not open*");
    }

    [Fact]
    public async Task Handle_InsufficientFunds_ThrowsInvalidOperationException()
    {
        await using var db = DbContextFactory.Create();
        var source = await SeedAccountAsync(db, balance: 30m);
        var target = await SeedAccountAsync(db);

        await Sut(db).Invoking(h => h.Handle(new TransferCommand(source.Id, target.Id, Amount: 50m, null), default))
            .Should().ThrowAsync<InvalidOperationException>()
            .WithMessage("Insufficient funds.");
    }
}
