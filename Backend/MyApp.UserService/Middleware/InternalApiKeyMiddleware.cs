public class InternalApiKeyMiddleware
{
    private const string HeaderName = "X-Internal-Api-Key";
    private readonly RequestDelegate _next;
    private readonly string _expectedKey;

    public InternalApiKeyMiddleware(RequestDelegate next, IConfiguration configuration)
    {
        _next = next;
        _expectedKey =
            configuration["InternalApiKey"]
            ?? throw new InvalidOperationException(
                "InternalApiKey is not configured in appsettings."
            );
    }

    public async Task InvokeAsync(HttpContext context)
    {
        // Allow Scalar UI, OpenAPI spec, and Swagger UI through without a key (dev convenience)
        if (
            context.Request.Path.StartsWithSegments("/scalar")
            || context.Request.Path.StartsWithSegments("/openapi")
            || context.Request.Path.StartsWithSegments("/swagger")
        )
        {
            await _next(context);
            return;
        }

        if (
            !context.Request.Headers.TryGetValue(HeaderName, out var providedKey)
            || !string.Equals(providedKey.ToString().Trim(), _expectedKey.Trim(), StringComparison.Ordinal)
        )
        {
            context.Response.StatusCode = StatusCodes.Status401Unauthorized;
            context.Response.ContentType = "application/json";
            await context.Response.WriteAsJsonAsync(
                new { error = "Invalid or missing internal API key." }
            );
            return;
        }

        await _next(context);
    }
}
