namespace MyApp.CoreService.ExchangeRates;

/// <summary>Used in Testing environment so consumers do not call the external FX API.</summary>
public sealed class NoOpExchangeRateService : IExchangeRateService
{
    public Task<decimal> GetRateAsync(string fromCurrency, string toCurrency, CancellationToken ct = default) =>
        Task.FromResult(1m);
}
