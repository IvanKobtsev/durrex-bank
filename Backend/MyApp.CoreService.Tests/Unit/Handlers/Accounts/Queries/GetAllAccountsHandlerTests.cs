using FluentAssertions;
using MyApp.CoreService.Data;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Features.Accounts.Queries.GetAllAccounts;
using MyApp.CoreService.Models;
using MyApp.CoreService.Tests.Unit.Helpers;

namespace MyApp.CoreService.Tests.Unit.Handlers.Accounts.Queries;

public class GetAllAccountsHandlerTests
{
    private static GetAllAccountsHandler Sut(CoreDbContext db) => new(db);

    [Fact]
    public async Task Handle_NoAccounts_ReturnsEmptyList()
    {
        await using var db = DbContextFactory.Create();

        var result = await Sut(db).Handle(new GetAllAccountsQuery(), default);

        result.Should().BeEmpty();
    }

    [Fact]
    public async Task Handle_MultipleAccounts_ReturnsAllOrderedById()
    {
        await using var db = DbContextFactory.Create();
        db.Accounts.AddRange(
            new Account { OwnerId = 1, Currency = "RUB", Status = AccountStatus.Open, CreatedAt = DateTimeOffset.UtcNow },
            new Account { OwnerId = 2, Currency = "USD", Status = AccountStatus.Open, CreatedAt = DateTimeOffset.UtcNow },
            new Account { OwnerId = 3, Currency = "EUR", Status = AccountStatus.Open, CreatedAt = DateTimeOffset.UtcNow }
        );
        await db.SaveChangesAsync();

        var result = await Sut(db).Handle(new GetAllAccountsQuery(), default);

        result.Should().HaveCount(3);
        result.Select(a => a.OwnerId).Should().BeEquivalentTo([1, 2, 3]);
    }
}
