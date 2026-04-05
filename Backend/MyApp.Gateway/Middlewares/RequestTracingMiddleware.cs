using System.Diagnostics;
using System.Security.Claims;

namespace MyApp.Gateway.Middlewares;

public sealed class RequestTracingMiddleware(RequestDelegate next)
{
    public const string MonitoringClientName = "MonitoringRequestTracing";

    private static readonly string[] ExcludedPathPrefixes =
    [
        "/_framework",
        "/_blazor",
        "/lib",
        "/favicon",
        "/css",
        "/js",
        "/images",
        "/swagger",
        "/monitoring",
    ];

    public async Task InvokeAsync(
        HttpContext context,
        IHttpClientFactory httpClientFactory,
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

            var traceRequest = new
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
                var client = httpClientFactory.CreateClient(MonitoringClientName);
                using var response = await client.PostAsJsonAsync(
                    "api/requests",
                    traceRequest,
                    context.RequestAborted
                );

                if (!response.IsSuccessStatusCode)
                {
                    logger.LogWarning(
                        "Monitoring service returned {StatusCode} when tracing {Method} {Path}",
                        (int)response.StatusCode,
                        context.Request.Method,
                        path
                    );
                }
            }
            catch (OperationCanceledException) when (context.RequestAborted.IsCancellationRequested)
            {
                logger.LogDebug(
                    "Skipping request trace submission because the request was aborted for {Method} {Path}",
                    context.Request.Method,
                    path
                );
            }
            catch (Exception ex)
            {
                logger.LogWarning(
                    ex,
                    "Failed to send request trace for {Method} {Path}",
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
        return ExcludedPathPrefixes.Any(prefix =>
            path.StartsWith(prefix, StringComparison.OrdinalIgnoreCase)
        );
    }
}
