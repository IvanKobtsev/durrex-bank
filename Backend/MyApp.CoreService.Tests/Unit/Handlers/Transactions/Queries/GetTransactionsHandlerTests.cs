using FluentAssertions;
using MyApp.CoreService.Data;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Features.Transactions.Queries.GetTransactions;
using MyApp.CoreService.Models;
using MyApp.CoreService.Tests.Unit.Helpers;

namespace MyApp.CoreService.Tests.Unit.Handlers.Transactions.Queries;

public class GetTransactionsHandlerTests
{
    private static GetTransactionsHandler Sut(CoreDbContext db) => new(db);

    private static async Task<Account> SeedAccountWithTransactionsAsync(
        CoreDbContext db, int transactionCount)
    {
        var account = new Account
        {
            OwnerId = 1, Currency = "RUB",
            Status = AccountStatus.Open,
            CreatedAt = DateTimeOffset.UtcNow
        };
        db.Accounts.Add(account);
        await db.SaveChangesAsync();

        for (var i = 0; i < transactionCount; i++)
        {
            db.Transactions.Add(new Transaction
            {
                AccountId = account.Id,
                Type = TransactionType.Deposit,
                Amount = 10m,
                BalanceBefore = i * 10m,
                BalanceAfter = (i + 1) * 10m,
                CreatedAt = DateTimeOffset.UtcNow.AddMinutes(i)
            });
        }
        await db.SaveChangesAsync();
        return account;
    }

    [Fact]
    public async Task Handle_NonExistentAccount_ThrowsKeyNotFoundException()
    {
        await using var db = DbContextFactory.Create();

        await Sut(db).Invoking(h => h.Handle(new GetTransactionsQuery(AccountId: 999, Page: 1, PageSize: 10), default))
            .Should().ThrowAsync<KeyNotFoundException>()
            .WithMessage("*999*");
    }

    [Fact]
    public async Task Handle_AccountWithNoTransactions_ReturnsEmptyPage()
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedAccountWithTransactionsAsync(db, transactionCount: 0);

        var result = await Sut(db).Handle(new GetTransactionsQuery(account.Id, Page: 1, PageSize: 10), default);

        result.Items.Should().BeEmpty();
        result.TotalCount.Should().Be(0);
        result.TotalPages.Should().Be(0);
    }

    [Fact]
    public async Task Handle_AccountWithTransactions_ReturnsPaginatedResults()
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedAccountWithTransactionsAsync(db, transactionCount: 15);

        var result = await Sut(db).Handle(new GetTransactionsQuery(account.Id, Page: 1, PageSize: 10), default);

        result.Items.Should().HaveCount(10);
        result.TotalCount.Should().Be(15);
        result.TotalPages.Should().Be(2);
        result.Page.Should().Be(1);
        result.PageSize.Should().Be(10);
    }

    [Fact]
    public async Task Handle_SecondPage_ReturnsRemainingItems()
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedAccountWithTransactionsAsync(db, transactionCount: 15);

        var result = await Sut(db).Handle(new GetTransactionsQuery(account.Id, Page: 2, PageSize: 10), default);

        result.Items.Should().HaveCount(5);
    }

    [Theory]
    [InlineData(0)]
    [InlineData(-5)]
    public async Task Handle_NonPositivePage_ClampedToOne(int page)
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedAccountWithTransactionsAsync(db, transactionCount: 3);

        var result = await Sut(db).Handle(new GetTransactionsQuery(account.Id, page, PageSize: 10), default);

        result.Page.Should().Be(1);
        result.Items.Should().HaveCount(3);
    }

    [Fact]
    public async Task Handle_PageSizeAboveMax_ClampedToOneHundred()
    {
        await using var db = DbContextFactory.Create();
        var account = await SeedAccountWithTransactionsAsync(db, transactionCount: 5);

        var result = await Sut(db).Handle(new GetTransactionsQuery(account.Id, Page: 1, PageSize: 200), default);

        result.PageSize.Should().Be(100);
    }
}
