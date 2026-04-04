using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using Microsoft.IdentityModel.Tokens;

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

        // var user = context.User;

        // if (user.Identity is null || !user.Identity.IsAuthenticated)
        // {
        //     context.Response.StatusCode = 401;
        //     await context.Response.WriteAsync("Missing or invalid Authorization header.");
        //     return;
        // }

        // var userId = user.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier)?.Value
        //           ?? user.FindFirst("sub")?.Value;
        //
        // var roles = user.FindAll("role").Select(c => c.Value).ToList();
        //
        // if (userId is null || roles.Count == 0)
        // {
        //     context.Response.StatusCode = 401;
        //     await context.Response.WriteAsync("Invalid token claims.");
        //     return;
        // }
        //
        // context.Request.Headers["X-User-Id"] = userId;
        // context.Request.Headers["X-User-Roles"] = string.Join(",", roles);
        //
        // await next(context);
        
        var authHeader = context.Request.Headers.Authorization.FirstOrDefault();

        if (authHeader is null || !authHeader.StartsWith("Bearer "))
        {
            context.Response.StatusCode = 401;
            await context.Response.WriteAsync("Missing or invalid Authorization header.");
            return;
        }

        var token = authHeader.Substring("Bearer ".Length);

        var handler = new JwtSecurityTokenHandler();

        try
        {
            var principal = handler.ValidateToken(
                token,
                new TokenValidationParameters
                {
                    ValidateIssuer = false,

                    ValidateAudience = false,

                    ValidateIssuerSigningKey = false,

                    ValidateLifetime = false,
                    ClockSkew = TimeSpan.FromSeconds(30),

                    ValidateActor = false
                },
                out var validatedToken
            );

            var userId = principal.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var role = principal.FindFirst(ClaimTypes.Role)?.Value;

            if (userId is null || role is null)
            {
                context.Response.StatusCode = 401;
                await context.Response.WriteAsync("Invalid token claims.");
                return;
            }

            // Inject headers for downstream services
            context.Request.Headers["X-User-Id"] = userId;
            context.Request.Headers["X-User-Role"] = role;

            await next(context);
        }
        // catch (SecurityTokenException)
        // {
        //     context.Response.StatusCode = 401;
        //     await context.Response.WriteAsync("Invalid or expired token.");
        // }
        catch (Exception ex)
        {
            Console.WriteLine(ex.ToString());
            throw;
        }
    }
}
