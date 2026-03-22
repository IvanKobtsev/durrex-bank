namespace MyApp.AuthService.Middleware;

public class InternalApiKeyMiddleware(RequestDelegate next, IConfiguration config)
{
    private const string HeaderName = "X-Internal-Api-Key";

    public async Task InvokeAsync(HttpContext context)
    {
        if (!context.Request.Path.StartsWithSegments("/internal"))
        {
            await next(context);
            return;
        }

        if (!context.Request.Headers.TryGetValue(HeaderName, out var key)
            || key != config["InternalApiKey"])
        {
            context.Response.StatusCode = 401;
            await context.Response.WriteAsync("Missing or invalid internal API key.");
            return;
        }

        await next(context);
    }
}
