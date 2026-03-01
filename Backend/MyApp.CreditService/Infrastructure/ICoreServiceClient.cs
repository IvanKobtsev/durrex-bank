public interface ICoreServiceClient
{
    Task DepositAsync(
        int accountId,
        decimal amount,
        string? description,
        CancellationToken ct = default
    );
    Task DebitAsync(
        int accountId,
        decimal amount,
        string? description,
        CancellationToken ct = default
    );
}
