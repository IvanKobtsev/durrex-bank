namespace MyApp.CoreService.ExchangeRates;

public class ExchangeRateRefresher(
    IServiceScopeFactory scopeFactory,
    IConfiguration config,
    ILogger<ExchangeRateRefresher> logger
) : BackgroundService
{
    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        var currencies = config.GetSection("ExchangeRates:SupportedCurrencies").Get<string[]>();
        if (currencies is null || currencies.Length == 0)
        {
            logger.LogWarning("ExchangeRates:SupportedCurrencies is empty; skipping rate refresher.");
            return;
        }

        while (!stoppingToken.IsCancellationRequested)
        {
            try
            {
                using var scope = scopeFactory.CreateScope();
                var exchangeRates = scope.ServiceProvider.GetRequiredService<IExchangeRateService>();

                foreach (var from in currencies)
                {
                    foreach (var to in currencies.Where(c => c != from))
                        await exchangeRates.GetRateAsync(from, to, stoppingToken);
                }
            }
            catch (Exception ex) when (ex is not OperationCanceledException)
            {
                logger.LogWarning(ex, "Exchange rate refresh failed.");
            }

            try
            {
                await Task.Delay(TimeSpan.FromMinutes(9), stoppingToken);
            }
            catch (OperationCanceledException)
            {
                break;
            }
        }
    }
}
