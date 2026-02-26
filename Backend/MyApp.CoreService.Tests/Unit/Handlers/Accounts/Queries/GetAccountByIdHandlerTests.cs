using FluentAssertions;
using MyApp.CoreService.Data;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Features.Accounts.Queries.GetAccountById;
using MyApp.CoreService.Models;
using MyApp.CoreService.Tests.Unit.Helpers;

namespace MyApp.CoreService.Tests.Unit.Handlers.Accounts.Queries;

public class GetAccountByIdHandlerTests
{
    private static GetAccountByIdHandler Sut(CoreDbContext db) => new(db);

    [Fact]
    public async Task Handle_ExistingAccount_ReturnsCorrectAccount()
    {
        await using var db = DbContextFactory.Create();
        var account = new Account
        {
            OwnerId = 7,
            Currency = "EUR",
            Status = AccountStatus.Open,
            CreatedAt = DateTimeOffset.UtcNow
        };
        db.Accounts.Add(account);
        await db.SaveChangesAsync();

        var result = await Sut(db).Handle(new GetAccountByIdQuery(account.Id), default);

        result.Id.Should().Be(account.Id);
        result.OwnerId.Should().Be(7);
        result.Currency.Should().Be("EUR");
    }

    [Fact]
    public async Task Handle_NonExistentAccount_ThrowsKeyNotFoundException()
    {
        await using var db = DbContextFactory.Create();

        await Sut(db).Invoking(h => h.Handle(new GetAccountByIdQuery(404), default))
            .Should().ThrowAsync<KeyNotFoundException>()
            .WithMessage("*404*");
    }
}
