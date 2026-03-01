using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using Microsoft.IdentityModel.Tokens;

public class JwtForwardingMiddleware
{
    private readonly RequestDelegate _next;
    private readonly TokenValidationParameters _validationParameters;

    public JwtForwardingMiddleware(
        RequestDelegate next,
        IConfiguration configuration,
        RsaSecurityKey rsaKey)
    {
        _next = next;

        _validationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidIssuer = configuration["Jwt:Issuer"],

            ValidateAudience = true,
            ValidAudience = configuration["Jwt:Audience"],

            ValidateIssuerSigningKey = true,
            IssuerSigningKey = rsaKey,

            ValidateLifetime = true,
            ClockSkew = TimeSpan.FromSeconds(30),

            ValidateActor = false
        };
    }

    public async Task InvokeAsync(HttpContext context)
    {
        var path = context.Request.Path;

        if (path.Equals("/user/auth/login") || path.ToString().EndsWith(".json"))
        {
            await _next(context);
            return;
        }
        
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
                _validationParameters,
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

            await _next(context);
        }
        catch (Exception)
        {
            context.Response.StatusCode = 401;
            await context.Response.WriteAsync("Invalid or expired token.");
        }
    }
}