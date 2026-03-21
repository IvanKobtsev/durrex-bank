namespace MyApp.CoreService.ExchangeRates;

public interface IExchangeRateService
{
    Task<decimal> GetRateAsync(string fromCurrency, string toCurrency, CancellationToken ct = default);
}
