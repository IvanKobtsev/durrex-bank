public class CoreServiceClient(HttpClient http) : ICoreServiceClient
{
    public async Task DebitAsync(
        int accountId,
        decimal amount,
        string? description,
        CancellationToken ct = default
    )
    {
        var response = await http.PostAsJsonAsync(
            $"api/accounts/{accountId}/debit",
            new { amount, description },
            ct
        );
        response.EnsureSuccessStatusCode();
    }

    public async Task DepositAsync(
        int accountId,
        decimal amount,
        string? description,
        CancellationToken ct = default
    )
    {
        var response = await http.PostAsJsonAsync(
            $"api/accounts/{accountId}/deposit",
            new { amount, description },
            ct
        );
        response.EnsureSuccessStatusCode();
    }
}
