using FluentAssertions;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Features.Accounts.Commands.CreateAccount;
using MyApp.CoreService.Tests.Unit.Helpers;

namespace MyApp.CoreService.Tests.Unit.Handlers.Accounts.Commands;

public class CreateAccountHandlerTests
{
    private static CreateAccountHandler Sut(MyApp.CoreService.Data.CoreDbContext db) => new(db);

    [Fact]
    public async Task Handle_ValidCurrency_ReturnsAccountResponse()
    {
        await using var db = DbContextFactory.Create();

        var result = await Sut(db).Handle(new CreateAccountCommand(OwnerId: 1, Currency: "USD"), default);

        result.OwnerId.Should().Be(1);
        result.Currency.Should().Be("USD");
        result.Balance.Should().Be(0m);
        result.Status.Should().Be(AccountStatus.Open);
    }

    [Fact]
    public async Task Handle_DefaultCurrency_UsesRUB()
    {
        await using var db = DbContextFactory.Create();

        var result = await Sut(db).Handle(new CreateAccountCommand(OwnerId: 5), default);

        result.Currency.Should().Be("RUB");
    }

    [Fact]
    public async Task Handle_LowercaseCurrency_NormalizesToUppercase()
    {
        await using var db = DbContextFactory.Create();

        var result = await Sut(db).Handle(new CreateAccountCommand(OwnerId: 1, Currency: "usd"), default);

        result.Currency.Should().Be("USD");
    }

    [Fact]
    public async Task Handle_ValidCurrency_PersistsAccountToDatabase()
    {
        await using var db = DbContextFactory.Create();

        await Sut(db).Handle(new CreateAccountCommand(OwnerId: 42, Currency: "EUR"), default);

        db.Accounts.Should().ContainSingle(a => a.OwnerId == 42 && a.Currency == "EUR");
    }

    [Theory]
    [InlineData("")]
    [InlineData("   ")]
    [InlineData("USDT")] // 4 characters — too long
    public async Task Handle_InvalidCurrency_ThrowsArgumentException(string currency)
    {
        await using var db = DbContextFactory.Create();

        await Sut(db).Invoking(h => h.Handle(new CreateAccountCommand(1, currency), default))
            .Should().ThrowAsync<ArgumentException>();
    }
}
