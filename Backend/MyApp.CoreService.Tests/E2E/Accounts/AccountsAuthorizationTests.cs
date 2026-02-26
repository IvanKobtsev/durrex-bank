using System.Net;
using System.Net.Http.Json;
using FluentAssertions;
using MyApp.CoreService.DTOs.Responses;

namespace MyApp.CoreService.Tests.E2E.Accounts;

public class AccountsAuthorizationTests : IClassFixture<CoreServiceFactory>
{
    private readonly CoreServiceFactory _factory;

    public AccountsAuthorizationTests(CoreServiceFactory factory)
    {
        _factory = factory;
    }

    /// <summary>Creates an account via the internal (no user headers) client — no ownership restrictions.</summary>
    private async Task<AccountResponse> SeedAccountAsync(int ownerId, string currency = "RUB")
    {
        var internalClient = _factory.CreateAuthenticatedClient();
        var response = await internalClient.PostAsJsonAsync("/api/accounts", new { OwnerId = ownerId, Currency = currency });
        return (await response.Content.ReadFromJsonAsync<AccountResponse>())!;
    }

    /// <summary>Deposits funds into an account via the internal client.</summary>
    private async Task DepositAsync(int accountId, decimal amount)
    {
        var internalClient = _factory.CreateAuthenticatedClient();
        await internalClient.PostAsJsonAsync($"/api/accounts/{accountId}/deposit", new { Amount = amount });
    }

    // ── POST /api/accounts ────────────────────────────────────────────────────

    [Fact]
    public async Task CreateAccount_AsClient_OverridesOwnerIdWithUserIdHeader()
    {
        var clientHttp = _factory.CreateClientUserClient(1001);

        // Body says OwnerId=9999, but the header says X-User-Id=1001 — header wins
        var response = await clientHttp.PostAsJsonAsync("/api/accounts", new { OwnerId = 9999, Currency = "RUB" });

        response.StatusCode.Should().Be(HttpStatusCode.Created);
        var account = await response.Content.ReadFromJsonAsync<AccountResponse>();
        account!.OwnerId.Should().Be(1001);
    }

    [Fact]
    public async Task CreateAccount_AsEmployee_UsesOwnerIdFromBody()
    {
        var employee = _factory.CreateEmployeeClient(1);

        var response = await employee.PostAsJsonAsync("/api/accounts", new { OwnerId = 1002, Currency = "RUB" });

        response.StatusCode.Should().Be(HttpStatusCode.Created);
        var account = await response.Content.ReadFromJsonAsync<AccountResponse>();
        account!.OwnerId.Should().Be(1002);
    }

    // ── GET /api/accounts ─────────────────────────────────────────────────────

    [Fact]
    public async Task GetAll_AsClient_ReturnsOnlyOwnAccounts()
    {
        await SeedAccountAsync(1010);
        await SeedAccountAsync(1011); // different owner

        var clientHttp = _factory.CreateClientUserClient(1010);
        var response = await clientHttp.GetAsync("/api/accounts");

        response.StatusCode.Should().Be(HttpStatusCode.OK);
        var accounts = await response.Content.ReadFromJsonAsync<List<AccountResponse>>();
        accounts.Should().OnlyContain(a => a.OwnerId == 1010);
    }

    [Fact]
    public async Task GetAll_AsClient_IgnoresOwnerIdQueryParam()
    {
        await SeedAccountAsync(1012);
        await SeedAccountAsync(1013);

        var clientHttp = _factory.CreateClientUserClient(1012);
        // ownerId=1013 in query should be ignored — client always sees only their own
        var response = await clientHttp.GetAsync("/api/accounts?ownerId=1013");

        response.StatusCode.Should().Be(HttpStatusCode.OK);
        var accounts = await response.Content.ReadFromJsonAsync<List<AccountResponse>>();
        accounts.Should().OnlyContain(a => a.OwnerId == 1012);
    }

    [Fact]
    public async Task GetAll_AsEmployee_ReturnsAccountsForAllUsers()
    {
        await SeedAccountAsync(1020);
        await SeedAccountAsync(1021);

        var employee = _factory.CreateEmployeeClient(1);
        var response = await employee.GetAsync("/api/accounts");

        response.StatusCode.Should().Be(HttpStatusCode.OK);
        var accounts = await response.Content.ReadFromJsonAsync<List<AccountResponse>>();
        accounts.Should().Contain(a => a.OwnerId == 1020);
        accounts.Should().Contain(a => a.OwnerId == 1021);
    }

    [Fact]
    public async Task GetAll_AsEmployee_WithOwnerIdFilter_ReturnsOnlyFilteredAccounts()
    {
        await SeedAccountAsync(1022);
        await SeedAccountAsync(1023);

        var employee = _factory.CreateEmployeeClient(1);
        var response = await employee.GetAsync("/api/accounts?ownerId=1022");

        response.StatusCode.Should().Be(HttpStatusCode.OK);
        var accounts = await response.Content.ReadFromJsonAsync<List<AccountResponse>>();
        accounts.Should().OnlyContain(a => a.OwnerId == 1022);
    }

    // ── GET /api/accounts/{id} ────────────────────────────────────────────────

    [Fact]
    public async Task GetById_AsClient_OwnAccount_Returns200()
    {
        var account = await SeedAccountAsync(1030);
        var clientHttp = _factory.CreateClientUserClient(1030);

        var response = await clientHttp.GetAsync($"/api/accounts/{account.Id}");

        response.StatusCode.Should().Be(HttpStatusCode.OK);
    }

    [Fact]
    public async Task GetById_AsClient_AnotherUsersAccount_Returns403()
    {
        var account = await SeedAccountAsync(1031);
        var clientHttp = _factory.CreateClientUserClient(1032); // different user

        var response = await clientHttp.GetAsync($"/api/accounts/{account.Id}");

        response.StatusCode.Should().Be(HttpStatusCode.Forbidden);
    }

    [Fact]
    public async Task GetById_AsEmployee_AnyAccount_Returns200()
    {
        var account = await SeedAccountAsync(1033);
        var employee = _factory.CreateEmployeeClient(1);

        var response = await employee.GetAsync($"/api/accounts/{account.Id}");

        response.StatusCode.Should().Be(HttpStatusCode.OK);
    }

    // ── DELETE /api/accounts/{id} ─────────────────────────────────────────────

    [Fact]
    public async Task CloseAccount_AsClient_OwnAccount_Returns200()
    {
        var account = await SeedAccountAsync(1040);
        var clientHttp = _factory.CreateClientUserClient(1040);

        var response = await clientHttp.DeleteAsync($"/api/accounts/{account.Id}");

        response.StatusCode.Should().Be(HttpStatusCode.OK);
    }

    [Fact]
    public async Task CloseAccount_AsClient_AnotherUsersAccount_Returns403()
    {
        var account = await SeedAccountAsync(1041);
        var clientHttp = _factory.CreateClientUserClient(1042); // different user

        var response = await clientHttp.DeleteAsync($"/api/accounts/{account.Id}");

        response.StatusCode.Should().Be(HttpStatusCode.Forbidden);
    }

    [Fact]
    public async Task CloseAccount_AsEmployee_AnyAccount_Returns200()
    {
        var account = await SeedAccountAsync(1043);
        var employee = _factory.CreateEmployeeClient(1);

        var response = await employee.DeleteAsync($"/api/accounts/{account.Id}");

        response.StatusCode.Should().Be(HttpStatusCode.OK);
    }

    // ── POST /api/accounts/{id}/deposit ──────────────────────────────────────

    [Fact]
    public async Task Deposit_AsClient_OwnAccount_Returns200()
    {
        var account = await SeedAccountAsync(1050);
        var clientHttp = _factory.CreateClientUserClient(1050);

        var response = await clientHttp.PostAsJsonAsync($"/api/accounts/{account.Id}/deposit",
            new { Amount = 100m });

        response.StatusCode.Should().Be(HttpStatusCode.OK);
    }

    [Fact]
    public async Task Deposit_AsClient_AnotherUsersAccount_Returns403()
    {
        var account = await SeedAccountAsync(1051);
        var clientHttp = _factory.CreateClientUserClient(1052); // different user

        var response = await clientHttp.PostAsJsonAsync($"/api/accounts/{account.Id}/deposit",
            new { Amount = 100m });

        response.StatusCode.Should().Be(HttpStatusCode.Forbidden);
    }

    [Fact]
    public async Task Deposit_AsEmployee_AnyAccount_Returns200()
    {
        var account = await SeedAccountAsync(1053);
        var employee = _factory.CreateEmployeeClient(1);

        var response = await employee.PostAsJsonAsync($"/api/accounts/{account.Id}/deposit",
            new { Amount = 100m });

        response.StatusCode.Should().Be(HttpStatusCode.OK);
    }

    // ── POST /api/accounts/{id}/withdraw ─────────────────────────────────────

    [Fact]
    public async Task Withdraw_AsClient_OwnAccount_Returns200()
    {
        var account = await SeedAccountAsync(1060);
        await DepositAsync(account.Id, 200m);
        var clientHttp = _factory.CreateClientUserClient(1060);

        var response = await clientHttp.PostAsJsonAsync($"/api/accounts/{account.Id}/withdraw",
            new { Amount = 50m });

        response.StatusCode.Should().Be(HttpStatusCode.OK);
    }

    [Fact]
    public async Task Withdraw_AsClient_AnotherUsersAccount_Returns403()
    {
        var account = await SeedAccountAsync(1061);
        await DepositAsync(account.Id, 200m);
        var clientHttp = _factory.CreateClientUserClient(1062); // different user

        var response = await clientHttp.PostAsJsonAsync($"/api/accounts/{account.Id}/withdraw",
            new { Amount = 50m });

        response.StatusCode.Should().Be(HttpStatusCode.Forbidden);
    }

    [Fact]
    public async Task Withdraw_AsEmployee_AnyAccount_Returns200()
    {
        var account = await SeedAccountAsync(1063);
        await DepositAsync(account.Id, 200m);
        var employee = _factory.CreateEmployeeClient(1);

        var response = await employee.PostAsJsonAsync($"/api/accounts/{account.Id}/withdraw",
            new { Amount = 50m });

        response.StatusCode.Should().Be(HttpStatusCode.OK);
    }

    // ── POST /api/accounts/{id}/transfer ─────────────────────────────────────

    [Fact]
    public async Task Transfer_AsClient_FromOwnAccount_Returns200()
    {
        var source = await SeedAccountAsync(1070);
        var target = await SeedAccountAsync(1071);
        await DepositAsync(source.Id, 300m);
        var clientHttp = _factory.CreateClientUserClient(1070);

        var response = await clientHttp.PostAsJsonAsync($"/api/accounts/{source.Id}/transfer",
            new { TargetAccountId = target.Id, Amount = 100m });

        response.StatusCode.Should().Be(HttpStatusCode.OK);
    }

    [Fact]
    public async Task Transfer_AsClient_FromAnotherUsersAccount_Returns403()
    {
        var source = await SeedAccountAsync(1072);
        var target = await SeedAccountAsync(1073);
        await DepositAsync(source.Id, 300m);
        var clientHttp = _factory.CreateClientUserClient(1074); // different user

        var response = await clientHttp.PostAsJsonAsync($"/api/accounts/{source.Id}/transfer",
            new { TargetAccountId = target.Id, Amount = 100m });

        response.StatusCode.Should().Be(HttpStatusCode.Forbidden);
    }

    [Fact]
    public async Task Transfer_AsEmployee_AnyAccount_Returns200()
    {
        var source = await SeedAccountAsync(1075);
        var target = await SeedAccountAsync(1076);
        await DepositAsync(source.Id, 300m);
        var employee = _factory.CreateEmployeeClient(1);

        var response = await employee.PostAsJsonAsync($"/api/accounts/{source.Id}/transfer",
            new { TargetAccountId = target.Id, Amount = 100m });

        response.StatusCode.Should().Be(HttpStatusCode.OK);
    }

    // ── GET /api/accounts/{id}/transactions ──────────────────────────────────

    [Fact]
    public async Task GetTransactions_AsClient_OwnAccount_Returns200()
    {
        var account = await SeedAccountAsync(1080);
        await DepositAsync(account.Id, 50m);
        var clientHttp = _factory.CreateClientUserClient(1080);

        var response = await clientHttp.GetAsync($"/api/accounts/{account.Id}/transactions");

        response.StatusCode.Should().Be(HttpStatusCode.OK);
    }

    [Fact]
    public async Task GetTransactions_AsClient_AnotherUsersAccount_Returns403()
    {
        var account = await SeedAccountAsync(1081);
        var clientHttp = _factory.CreateClientUserClient(1082); // different user

        var response = await clientHttp.GetAsync($"/api/accounts/{account.Id}/transactions");

        response.StatusCode.Should().Be(HttpStatusCode.Forbidden);
    }

    [Fact]
    public async Task GetTransactions_AsEmployee_AnyAccount_Returns200()
    {
        var account = await SeedAccountAsync(1083);
        var employee = _factory.CreateEmployeeClient(1);

        var response = await employee.GetAsync($"/api/accounts/{account.Id}/transactions");

        response.StatusCode.Should().Be(HttpStatusCode.OK);
    }
}
