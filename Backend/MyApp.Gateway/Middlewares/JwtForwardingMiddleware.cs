namespace MyApp.Gateway.Middlewares;

public class JwtForwardingMiddleware(RequestDelegate next)
{
    private static readonly string[] PublicPrefixes =
    [
        "/auth/connect/",
        "/auth/.well-known/",
        "/auth/account/",
    ];

    private static bool IsPublicPath(PathString path)
    {
        var p = path.ToString();
        return PublicPrefixes.Any(prefix =>
                   p.StartsWith(prefix, StringComparison.OrdinalIgnoreCase))
               || p.EndsWith(".json", StringComparison.OrdinalIgnoreCase);
    }

    public async Task InvokeAsync(HttpContext context)
    {
        if (IsPublicPath(context.Request.Path))
        {
            await next(context);
            return;
        }

        var user = context.User;

        if (user.Identity is null || !user.Identity.IsAuthenticated)
        {
            context.Response.StatusCode = 401;
            await context.Response.WriteAsync("Missing or invalid Authorization header.");
            return;
        }

        var userId = user.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier)?.Value
                     ?? user.FindFirst("sub")?.Value;

        var roles = user.FindAll("role").Select(c => c.Value).ToList();

        if (userId is null || roles.Count == 0)
        {
            context.Response.StatusCode = 401;
            await context.Response.WriteAsync("Invalid token claims.");
            return;
        }

        context.Request.Headers["X-User-Id"] = userId;
        context.Request.Headers["X-User-Roles"] = string.Join(",", roles);

        await next(context);
    }
}