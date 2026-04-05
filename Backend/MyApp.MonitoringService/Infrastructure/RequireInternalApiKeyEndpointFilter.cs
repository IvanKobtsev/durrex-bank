namespace MyApp.MonitoringService.Infrastructure;

public sealed class RequireInternalApiKeyEndpointFilter(IConfiguration configuration)
    : IEndpointFilter
{
    private const string HeaderName = "X-Internal-Api-Key";
    private readonly string _expectedKey =
        configuration["InternalApiKey"]
        ?? throw new InvalidOperationException("InternalApiKey is not configured in appsettings.");

    public async ValueTask<object?> InvokeAsync(
        EndpointFilterInvocationContext context,
        EndpointFilterDelegate next
    )
    {
        var request = context.HttpContext.Request;

        if (
            !request.Headers.TryGetValue(HeaderName, out var providedKey)
            || !string.Equals(providedKey, _expectedKey, StringComparison.Ordinal)
        )
        {
            return Results.Json(
                new { error = "Invalid or missing internal API key." },
                statusCode: StatusCodes.Status401Unauthorized
            );
        }

        return await next(context);
    }
}
