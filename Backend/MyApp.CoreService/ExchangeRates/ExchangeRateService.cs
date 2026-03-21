using System.Text.Json;
using Microsoft.Extensions.Caching.Memory;

namespace MyApp.CoreService.ExchangeRates;

public class ExchangeRateService(
    HttpClient http,
    IMemoryCache cache,
    IConfiguration config
) : IExchangeRateService
{
    public async Task<decimal> GetRateAsync(string fromCurrency, string toCurrency, CancellationToken ct = default)
    {
        if (string.Equals(fromCurrency, toCurrency, StringComparison.OrdinalIgnoreCase))
            return 1m;

        var from = fromCurrency.ToUpperInvariant();
        var to = toCurrency.ToUpperInvariant();
        var cacheKey = $"rates:{from}:{to}";
        if (cache.TryGetValue(cacheKey, out decimal cached))
            return cached;

        var apiKey = config["ExchangeRates:ApiKey"];
        if (string.IsNullOrWhiteSpace(apiKey))
            throw new InvalidOperationException("ExchangeRates:ApiKey is not configured.");

        var baseUrl = config["ExchangeRates:BaseUrl"] ?? "https://v6.exchangerate-api.com";
        var url = $"{baseUrl.TrimEnd('/')}/v6/{apiKey}/latest/{from}";

        using var response = await http.GetAsync(url, ct);
        response.EnsureSuccessStatusCode();

        await using var stream = await response.Content.ReadAsStreamAsync(ct);
        using var doc = await JsonDocument.ParseAsync(stream, cancellationToken: ct);
        var root = doc.RootElement;
        if (root.TryGetProperty("result", out var resultEl) && resultEl.GetString() != "success")
            throw new InvalidOperationException("Exchange rate API returned an error.");

        if (!root.TryGetProperty("conversion_rates", out var rates))
            throw new InvalidOperationException("Exchange rate API response missing conversion_rates.");

        decimal? rate = null;
        foreach (var prop in rates.EnumerateObject())
        {
            if (string.Equals(prop.Name, to, StringComparison.OrdinalIgnoreCase))
            {
                rate = prop.Value.GetDecimal();
                break;
            }
        }

        if (rate is null)
            throw new InvalidOperationException($"Rate for {toCurrency} not found in API response.");

        var minutes = config.GetValue("ExchangeRates:CacheMinutes", 10);
        cache.Set(cacheKey, rate.Value, TimeSpan.FromMinutes(minutes));

        return rate.Value;
    }
}
