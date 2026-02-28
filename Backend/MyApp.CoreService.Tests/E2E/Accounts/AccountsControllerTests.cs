using System.Net;
using System.Net.Http.Json;
using FluentAssertions;
using MyApp.CoreService.DTOs.Responses;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Tests.E2E;

namespace MyApp.CoreService.Tests.E2E.Accounts;

public class AccountsControllerTests : IClassFixture<CoreServiceFactory>
{
    private readonly CoreServiceFactory _factory;
    private readonly HttpClient _client;

    public AccountsControllerTests(CoreServiceFactory factory)
    {
        _factory = factory;
        _client = factory.CreateAuthenticatedClient();
    }

    // ── Authentication ────────────────────────────────────────────────────────

    [Fact]
    public async Task AnyEndpoint_WithoutApiKey_Returns401()
    {
        // Use the factory to get a client that routes through the test server, but without the API key
        using var unauthClient = _factory.CreateClient();

        var response = await unauthClient.GetAsync("/api/accounts");

        response.StatusCode.Should().Be(HttpStatusCode.Unauthorized);
    }

    // ── POST /api/accounts ────────────────────────────────────────────────────

    [Fact]
    public async Task CreateAccount_ValidRequest_Returns201WithAccount()
    {
        var response = await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 1, Currency = "USD" });

        response.StatusCode.Should().Be(HttpStatusCode.Created);
        var account = await response.Content.ReadFromJsonAsync<AccountResponse>();
        account.Should().NotBeNull();
        account!.OwnerId.Should().Be(1);
        account.Currency.Should().Be("USD");
        account.Balance.Should().Be(0m);
        account.Status.Should().Be(AccountStatus.Open);
    }

    [Fact]
    public async Task CreateAccount_InvalidCurrency_Returns400()
    {
        var response = await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 1, Currency = "TOOLONG" });

        response.StatusCode.Should().Be(HttpStatusCode.BadRequest);
    }

    // ── GET /api/accounts ─────────────────────────────────────────────────────

    [Fact]
    public async Task GetAll_ReturnsListOfAccounts()
    {
        await _client.PostAsJsonAsync("/api/accounts", new { OwnerId = 100, Currency = "RUB" });

        var response = await _client.GetAsync("/api/accounts");

        response.StatusCode.Should().Be(HttpStatusCode.OK);
        var accounts = await response.Content.ReadFromJsonAsync<List<AccountResponse>>();
        accounts.Should().NotBeNull();
        accounts!.Should().Contain(a => a.OwnerId == 100);
    }

    [Fact]
    public async Task GetAll_FilterByOwnerId_ReturnsOnlyThatOwnersAccounts()
    {
        await _client.PostAsJsonAsync("/api/accounts", new { OwnerId = 200, Currency = "RUB" });
        await _client.PostAsJsonAsync("/api/accounts", new { OwnerId = 201, Currency = "USD" });

        var response = await _client.GetAsync("/api/accounts?ownerId=200");

        response.StatusCode.Should().Be(HttpStatusCode.OK);
        var accounts = await response.Content.ReadFromJsonAsync<List<AccountResponse>>();
        accounts.Should().OnlyContain(a => a.OwnerId == 200);
    }

    // ── GET /api/accounts/{id} ────────────────────────────────────────────────

    [Fact]
    public async Task GetById_ExistingAccount_Returns200()
    {
        var created = await (await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 300, Currency = "EUR" }))
            .Content.ReadFromJsonAsync<AccountResponse>();

        var response = await _client.GetAsync($"/api/accounts/{created!.Id}");

        response.StatusCode.Should().Be(HttpStatusCode.OK);
        var account = await response.Content.ReadFromJsonAsync<AccountResponse>();
        account!.Id.Should().Be(created.Id);
    }

    [Fact]
    public async Task GetById_NonExistentAccount_Returns404()
    {
        var response = await _client.GetAsync("/api/accounts/999999");

        response.StatusCode.Should().Be(HttpStatusCode.NotFound);
    }

    // ── DELETE /api/accounts/{id} (Close) ────────────────────────────────────

    [Fact]
    public async Task CloseAccount_EmptyBalance_Returns200WithClosedStatus()
    {
        var created = await (await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 400, Currency = "RUB" }))
            .Content.ReadFromJsonAsync<AccountResponse>();

        var response = await _client.DeleteAsync($"/api/accounts/{created!.Id}");

        response.StatusCode.Should().Be(HttpStatusCode.OK);
        var account = await response.Content.ReadFromJsonAsync<AccountResponse>();
        account!.Status.Should().Be(AccountStatus.Closed);
        account.ClosedAt.Should().NotBeNull();
    }

    [Fact]
    public async Task CloseAccount_NonZeroBalance_Returns400()
    {
        var created = await (await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 401, Currency = "RUB" }))
            .Content.ReadFromJsonAsync<AccountResponse>();

        // Deposit money first
        await _client.PostAsJsonAsync($"/api/accounts/{created!.Id}/deposit",
            new { Amount = 100m });

        var response = await _client.DeleteAsync($"/api/accounts/{created.Id}");

        response.StatusCode.Should().Be(HttpStatusCode.BadRequest);
    }

    // ── POST /api/accounts/{id}/deposit ──────────────────────────────────────

    [Fact]
    public async Task Deposit_ValidAmount_Returns200WithTransaction()
    {
        var created = await (await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 500, Currency = "RUB" }))
            .Content.ReadFromJsonAsync<AccountResponse>();

        var response = await _client.PostAsJsonAsync($"/api/accounts/{created!.Id}/deposit",
            new { Amount = 250m, Description = "Salary" });

        response.StatusCode.Should().Be(HttpStatusCode.OK);
        var tx = await response.Content.ReadFromJsonAsync<TransactionResponse>();
        tx!.Amount.Should().Be(250m);
        tx.BalanceBefore.Should().Be(0m);
        tx.BalanceAfter.Should().Be(250m);
        tx.Type.Should().Be(TransactionType.Deposit);
    }

    [Fact]
    public async Task Deposit_NegativeAmount_Returns400()
    {
        var created = await (await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 501, Currency = "RUB" }))
            .Content.ReadFromJsonAsync<AccountResponse>();

        var response = await _client.PostAsJsonAsync($"/api/accounts/{created!.Id}/deposit",
            new { Amount = -50m });

        response.StatusCode.Should().Be(HttpStatusCode.BadRequest);
    }

    // ── POST /api/accounts/{id}/withdraw ─────────────────────────────────────

    [Fact]
    public async Task Withdraw_SufficientFunds_Returns200WithTransaction()
    {
        var created = await (await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 600, Currency = "RUB" }))
            .Content.ReadFromJsonAsync<AccountResponse>();

        await _client.PostAsJsonAsync($"/api/accounts/{created!.Id}/deposit", new { Amount = 300m });

        var response = await _client.PostAsJsonAsync($"/api/accounts/{created.Id}/withdraw",
            new { Amount = 100m, Description = "Groceries" });

        response.StatusCode.Should().Be(HttpStatusCode.OK);
        var tx = await response.Content.ReadFromJsonAsync<TransactionResponse>();
        tx!.Amount.Should().Be(100m);
        tx.BalanceBefore.Should().Be(300m);
        tx.BalanceAfter.Should().Be(200m);
        tx.Type.Should().Be(TransactionType.Withdrawal);
    }

    [Fact]
    public async Task Withdraw_InsufficientFunds_Returns400()
    {
        var created = await (await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 601, Currency = "RUB" }))
            .Content.ReadFromJsonAsync<AccountResponse>();

        var response = await _client.PostAsJsonAsync($"/api/accounts/{created!.Id}/withdraw",
            new { Amount = 1000m });

        response.StatusCode.Should().Be(HttpStatusCode.BadRequest);
    }

    // ── POST /api/accounts/{id}/transfer ─────────────────────────────────────

    [Fact]
    public async Task Transfer_ValidAccounts_Returns200WithSourceTransaction()
    {
        var source = await (await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 700, Currency = "RUB" }))
            .Content.ReadFromJsonAsync<AccountResponse>();

        var target = await (await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 701, Currency = "RUB" }))
            .Content.ReadFromJsonAsync<AccountResponse>();

        await _client.PostAsJsonAsync($"/api/accounts/{source!.Id}/deposit", new { Amount = 500m });

        var response = await _client.PostAsJsonAsync($"/api/accounts/{source.Id}/transfer",
            new { TargetAccountId = target!.Id, Amount = 200m, Description = "Split bill" });

        response.StatusCode.Should().Be(HttpStatusCode.OK);
        var tx = await response.Content.ReadFromJsonAsync<TransactionResponse>();
        tx!.Amount.Should().Be(200m);
        tx.Type.Should().Be(TransactionType.Transfer);
        tx.RelatedAccountId.Should().Be(target.Id);
    }

    [Fact]
    public async Task Transfer_InsufficientFunds_Returns400()
    {
        var source = await (await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 702, Currency = "RUB" }))
            .Content.ReadFromJsonAsync<AccountResponse>();

        var target = await (await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 703, Currency = "RUB" }))
            .Content.ReadFromJsonAsync<AccountResponse>();

        var response = await _client.PostAsJsonAsync($"/api/accounts/{source!.Id}/transfer",
            new { TargetAccountId = target!.Id, Amount = 9999m });

        response.StatusCode.Should().Be(HttpStatusCode.BadRequest);
    }

    // ── POST /api/accounts/{id}/debit ────────────────────────────────────────

    [Fact]
    public async Task Debit_SufficientFunds_Returns200WithCreditRepaymentTransaction()
    {
        var created = await (await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 900, Currency = "RUB" }))
            .Content.ReadFromJsonAsync<AccountResponse>();

        await _client.PostAsJsonAsync($"/api/accounts/{created!.Id}/deposit", new { Amount = 500m });

        var response = await _client.PostAsJsonAsync($"/api/accounts/{created.Id}/debit",
            new { Amount = 150m, Description = "Credit repayment" });

        response.StatusCode.Should().Be(HttpStatusCode.OK);
        var tx = await response.Content.ReadFromJsonAsync<TransactionResponse>();
        tx!.Amount.Should().Be(150m);
        tx.BalanceBefore.Should().Be(500m);
        tx.BalanceAfter.Should().Be(350m);
        tx.Type.Should().Be(TransactionType.CreditRepayment);
    }

    [Fact]
    public async Task Debit_InsufficientFunds_Returns400()
    {
        var created = await (await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 901, Currency = "RUB" }))
            .Content.ReadFromJsonAsync<AccountResponse>();

        var response = await _client.PostAsJsonAsync($"/api/accounts/{created!.Id}/debit",
            new { Amount = 1000m });

        response.StatusCode.Should().Be(HttpStatusCode.BadRequest);
    }

    [Fact]
    public async Task Debit_NegativeAmount_Returns400()
    {
        var created = await (await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 902, Currency = "RUB" }))
            .Content.ReadFromJsonAsync<AccountResponse>();

        var response = await _client.PostAsJsonAsync($"/api/accounts/{created!.Id}/debit",
            new { Amount = -50m });

        response.StatusCode.Should().Be(HttpStatusCode.BadRequest);
    }

    [Fact]
    public async Task Debit_NonExistentAccount_Returns404()
    {
        var response = await _client.PostAsJsonAsync("/api/accounts/999999/debit",
            new { Amount = 100m });

        response.StatusCode.Should().Be(HttpStatusCode.NotFound);
    }

    // ── GET /api/accounts/{id}/transactions ──────────────────────────────────

    [Fact]
    public async Task GetTransactions_AfterDeposits_ReturnsPaginatedHistory()
    {
        var created = await (await _client.PostAsJsonAsync("/api/accounts",
            new { OwnerId = 800, Currency = "RUB" }))
            .Content.ReadFromJsonAsync<AccountResponse>();

        for (var i = 0; i < 5; i++)
            await _client.PostAsJsonAsync($"/api/accounts/{created!.Id}/deposit", new { Amount = 10m });

        var response = await _client.GetAsync($"/api/accounts/{created!.Id}/transactions?page=1&pageSize=3");

        response.StatusCode.Should().Be(HttpStatusCode.OK);
        var paged = await response.Content.ReadFromJsonAsync<PagedResponse<TransactionResponse>>();
        paged!.Items.Should().HaveCount(3);
        paged.TotalCount.Should().Be(5);
        paged.TotalPages.Should().Be(2);
    }

    [Fact]
    public async Task GetTransactions_NonExistentAccount_Returns404()
    {
        var response = await _client.GetAsync("/api/accounts/999999/transactions");

        response.StatusCode.Should().Be(HttpStatusCode.NotFound);
    }
}
