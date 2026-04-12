using MyApp.UserService.DTOs;
using MyApp.UserService.Infrastructure;

namespace MyApp.UserService.Middleware;

public class RandomFailureMiddleware(RequestDelegate next)
{
    private static readonly string[] SkipPrefixes =
        ["/scalar", "/openapi", "/swagger", "/hubs"];

    public async Task InvokeAsync(HttpContext context, MonitoringClient monitoringClient)
    {
        var path = context.Request.Path.Value ?? "/";

        if (!SkipPrefixes.Any(p => path.StartsWith(p, StringComparison.OrdinalIgnoreCase)))
        {
            var minute = DateTime.UtcNow.Minute;
            var threshold = minute % 2 == 0 ? 0.70 : 0.30;

            if (Random.Shared.NextDouble() < threshold)
            {
                context.Response.StatusCode = StatusCodes.Status500InternalServerError;
                context.Response.ContentType = "application/json";

                _ = monitoringClient.CaptureErrorAsync(new CaptureErrorEventDto
                {
                    Service = "UserService",
                    Environment = "Production",
                    Level = "Warning",
                    Message = "Simulated random failure (chaos engineering)",
                    RequestMethod = context.Request.Method,
                    RequestPath = path,
                    OccurredAtUtc = DateTimeOffset.UtcNow,
                });

                await context.Response.WriteAsJsonAsync(
                    new { error = "Service temporarily unavailable." });
                return;
            }
        }

        await next(context);
    }
}
