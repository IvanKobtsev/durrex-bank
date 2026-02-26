using FluentAssertions;
using MyApp.CoreService.Data;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Features.Accounts.Queries.GetAccountsByOwnerId;
using MyApp.CoreService.Models;
using MyApp.CoreService.Tests.Unit.Helpers;

namespace MyApp.CoreService.Tests.Unit.Handlers.Accounts.Queries;

public class GetAccountsByOwnerIdHandlerTests
{
    private static GetAccountsByOwnerIdHandler Sut(CoreDbContext db) => new(db);

    [Fact]
    public async Task Handle_AccountsForOwner_ReturnsFilteredResults()
    {
        await using var db = DbContextFactory.Create();
        db.Accounts.AddRange(
            new Account { OwnerId = 10, Currency = "RUB", Status = AccountStatus.Open, CreatedAt = DateTimeOffset.UtcNow },
            new Account { OwnerId = 10, Currency = "USD", Status = AccountStatus.Open, CreatedAt = DateTimeOffset.UtcNow.AddMinutes(1) },
            new Account { OwnerId = 99, Currency = "EUR", Status = AccountStatus.Open, CreatedAt = DateTimeOffset.UtcNow }
        );
        await db.SaveChangesAsync();

        var result = await Sut(db).Handle(new GetAccountsByOwnerIdQuery(OwnerId: 10), default);

        result.Should().HaveCount(2);
        result.Should().OnlyContain(a => a.OwnerId == 10);
    }

    [Fact]
    public async Task Handle_AccountsForOwner_ReturnsOrderedByCreatedAt()
    {
        await using var db = DbContextFactory.Create();
        var earlier = DateTimeOffset.UtcNow.AddHours(-1);
        var later   = DateTimeOffset.UtcNow;
        db.Accounts.AddRange(
            new Account { OwnerId = 5, Currency = "USD", Status = AccountStatus.Open, CreatedAt = later },
            new Account { OwnerId = 5, Currency = "RUB", Status = AccountStatus.Open, CreatedAt = earlier }
        );
        await db.SaveChangesAsync();

        var result = await Sut(db).Handle(new GetAccountsByOwnerIdQuery(OwnerId: 5), default);

        result.Should().HaveCount(2);
        result[0].Currency.Should().Be("RUB"); // earlier first
        result[1].Currency.Should().Be("USD");
    }

    [Fact]
    public async Task Handle_NoAccountsForOwner_ReturnsEmpty()
    {
        await using var db = DbContextFactory.Create();

        var result = await Sut(db).Handle(new GetAccountsByOwnerIdQuery(OwnerId: 1), default);

        result.Should().BeEmpty();
    }
}
