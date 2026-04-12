using System.Text.Json;
using StackExchange.Redis;

namespace MyApp.UserService.Middleware;

public sealed class IdempotencyMiddleware(RequestDelegate next, IConnectionMultiplexer redis)
{
    private const string SentinelValue = "__in_progress__";

    private static readonly HashSet<string> MutatingMethods =
        new(StringComparer.OrdinalIgnoreCase) { "POST", "PUT", "PATCH", "DELETE" };

    private sealed record CachedResponse(int StatusCode, string? ContentType, string Body);

    public async Task InvokeAsync(HttpContext context)
    {
        if (!MutatingMethods.Contains(context.Request.Method))
        {
            await next(context);
            return;
        }

        if (!context.Request.Headers.TryGetValue("Idempotency-Key", out var rawKey)
            || string.IsNullOrWhiteSpace(rawKey))
        {
            context.Response.StatusCode = StatusCodes.Status422UnprocessableEntity;
            await context.Response.WriteAsJsonAsync(
                new { error = "Idempotency-Key header is required for mutating requests." });
            return;
        }

        var method = context.Request.Method.ToUpperInvariant();
        var path = context.Request.Path.Value?.ToLowerInvariant() ?? "/";
        var cacheKey = $"idempotency:{method}:{path}:{rawKey}";

        var db = redis.GetDatabase();

        // Atomic: SET cacheKey SentinelValue EX 30 NX
        // Returns true only if the key did not exist — we are the first to claim it.
        var claimed = await db.StringSetAsync(
            cacheKey,
            SentinelValue,
            TimeSpan.FromSeconds(30),
            When.NotExists);

        if (!claimed)
        {
            var existing = await db.StringGetAsync(cacheKey);

            if (!existing.HasValue || existing == SentinelValue)
            {
                // Another instance is processing the same key right now
                context.Response.StatusCode = StatusCodes.Status409Conflict;
                await context.Response.WriteAsJsonAsync(
                    new { error = "A request with this Idempotency-Key is already being processed." });
                return;
            }

            // Found a stored 2xx response — replay it without re-executing the handler
            var hit = JsonSerializer.Deserialize<CachedResponse>((string)existing!);
            if (hit is not null)
            {
                context.Response.StatusCode = hit.StatusCode;
                context.Response.ContentType = hit.ContentType;
                await context.Response.WriteAsync(hit.Body);
                return;
            }

            // Corrupt entry: delete and let the request proceed as new
            await db.KeyDeleteAsync(cacheKey);
        }

        // We own the sentinel — capture the response body
        var originalBody = context.Response.Body;
        using var buffer = new MemoryStream();
        context.Response.Body = buffer;

        try
        {
            await next(context);

            buffer.Seek(0, SeekOrigin.Begin);
            var body = await new StreamReader(buffer).ReadToEndAsync();

            if (context.Response.StatusCode is >= 200 and < 300)
            {
                // Cache only successful responses — non-2xx must remain retryable
                var entry = JsonSerializer.Serialize(
                    new CachedResponse(context.Response.StatusCode, context.Response.ContentType, body));
                await db.StringSetAsync(cacheKey, entry, TimeSpan.FromHours(24));
            }
            else
            {
                // Remove sentinel immediately so the client can retry after fixing the problem
                await db.KeyDeleteAsync(cacheKey);
            }

            buffer.Seek(0, SeekOrigin.Begin);
            await buffer.CopyToAsync(originalBody);
        }
        catch
        {
            // Remove sentinel on unhandled exception — the upstream error handler will respond
            await db.KeyDeleteAsync(cacheKey);
            throw;
        }
        finally
        {
            context.Response.Body = originalBody;
        }
    }
}
