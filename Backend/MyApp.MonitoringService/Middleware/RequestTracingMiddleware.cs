using System.Diagnostics;
using System.Security.Claims;
using MyApp.MonitoringService.DTOs;
using MyApp.MonitoringService.Services;

namespace MyApp.MonitoringService.Middleware;

public sealed class RequestTracingMiddleware(RequestDelegate next)
{
    private static readonly string[] ExcludedPathPrefixes =
    [
        "/_framework",
        "/_blazor",
        "/lib",
        "/favicon",
        "/css",
        "/js",
        "/images",
    ];

    public async Task InvokeAsync(
        HttpContext context,
        MonitoringEventService monitoringEventService,
        ILogger<RequestTracingMiddleware> logger,
        IConfiguration configuration
    )
    {
        var tracingEnabled =
            configuration.GetValue<bool?>("Monitoring:RequestTracing:Enabled") ?? true;
        if (!tracingEnabled)
        {
            await next(context);
            return;
        }

        var path = context.Request.Path.Value ?? "/";
        if (ShouldSkip(path))
        {
            await next(context);
            return;
        }

        var stopwatch = Stopwatch.StartNew();
        Exception? caughtException = null;

        try
        {
            await next(context);
        }
        catch (Exception ex)
        {
            caughtException = ex;
            throw;
        }
        finally
        {
            stopwatch.Stop();

            var traceRequest = new CaptureRequestTraceRequest
            {
                Method = context.Request.Method,
                Path = path,
                QueryString = context.Request.QueryString.HasValue
                    ? context.Request.QueryString.Value
                    : null,
                StatusCode = context.Response.StatusCode,
                DurationMs = stopwatch.Elapsed.TotalMilliseconds,
                TraceId = Activity.Current?.TraceId.ToString() ?? context.TraceIdentifier,
                UserId = ResolveUserId(context.User, context.Request),
                RemoteIp = context.Connection.RemoteIpAddress?.ToString(),
                UserAgent = context.Request.Headers.UserAgent.ToString(),
                ExceptionType = caughtException?.GetType().FullName,
                ExceptionMessage = caughtException?.Message,
                TimestampUtc = DateTimeOffset.UtcNow,
            };

            try
            {
                await monitoringEventService.CaptureRequestTraceAsync(
                    traceRequest,
                    context.RequestAborted
                );
            }
            catch (Exception ex)
            {
                logger.LogWarning(
                    ex,
                    "Failed to store request trace for {Method} {Path}",
                    context.Request.Method,
                    path
                );
            }
        }
    }

    private static string? ResolveUserId(ClaimsPrincipal user, HttpRequest request)
    {
        var fromClaim =
            user.FindFirstValue(ClaimTypes.NameIdentifier) ?? user.FindFirstValue("sub");
        if (!string.IsNullOrWhiteSpace(fromClaim))
        {
            return fromClaim;
        }

        if (request.Headers.TryGetValue("X-User-Id", out var userIdHeader))
        {
            return userIdHeader.ToString();
        }

        return null;
    }

    private static bool ShouldSkip(string path)
    {
        if (
            path.StartsWith("/api/events", StringComparison.OrdinalIgnoreCase)
            || path.StartsWith("/api/requests", StringComparison.OrdinalIgnoreCase)
        )
        {
            return false;
        }

        return ExcludedPathPrefixes.Any(prefix =>
            path.StartsWith(prefix, StringComparison.OrdinalIgnoreCase)
        );
    }
}
