using MyApp.UserService.DTOs;

namespace MyApp.UserService.Infrastructure;

public class MonitoringClient(HttpClient httpClient, ILogger<MonitoringClient> logger)
{
    public async Task CaptureErrorAsync(CaptureErrorEventDto dto, CancellationToken ct = default)
    {
        try
        {
            await httpClient.PostAsJsonAsync("api/events", dto, ct);
        }
        catch (Exception ex)
        {
            logger.LogWarning(ex, "Failed to send error event to MonitoringService.");
        }
    }
}
