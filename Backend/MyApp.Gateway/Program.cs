using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using MyApp.Gateway;
using MyApp.Gateway.Middlewares;
using Yarp.ReverseProxy.Transforms;

var builder = WebApplication.CreateBuilder(args);

builder.Logging.AddConsole();

builder.Services.AddReverseProxy()
    .LoadFromConfig(builder.Configuration.GetSection("ReverseProxy"))
    .AddTransforms(builderContext =>
    {
        builderContext.AddRequestTransform(transformContext =>
        {
            // AuthService is a public OIDC server — do not inject the internal key
            if (!transformContext.HttpContext.Request.Path
                    .StartsWithSegments("/auth", StringComparison.OrdinalIgnoreCase))
            {
                transformContext.ProxyRequest.Headers.TryAddWithoutValidation(
                    "X-Internal-Api-Key",
                    builder.Configuration["InternalApiKey"]);
            }
            return ValueTask.CompletedTask;
        });

        SwaggerResponseTransformUtil.AddTransformIfMatch(builderContext);
    });

builder.Services.AddAuthentication("Bearer")
    .AddJwtBearer("Bearer", options =>
    {
        // AuthService discovery doc: {Authority}/.well-known/openid-configuration
        // JWKS fetched automatically, cached, and auto-rotated
        options.Authority = builder.Configuration["Oidc:Authority"];
        options.RequireHttpsMetadata = false;   // HTTP allowed in dev; enforce HTTPS in production
        options.MapInboundClaims = false;       // Keep short JWT claim names (sub, role) as-is

        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidAudiences = [builder.Configuration["Oidc:Audience"]!],
            ValidateLifetime = true,
            ClockSkew = TimeSpan.FromSeconds(30),
            NameClaimType = "sub",
            RoleClaimType = "role"
        };

        // Handle WebSocket connections: browsers pass token in query string
        options.Events = new JwtBearerEvents
        {
            OnMessageReceived = context =>
            {
                var accessToken = context.Request.Query["access_token"];
                var path = context.HttpContext.Request.Path;
                if (!string.IsNullOrEmpty(accessToken) &&
                    path.StartsWithSegments("/core/hubs"))
                {
                    context.Token = accessToken;
                }
                return Task.CompletedTask;
            },
        };
    });

builder.Services.AddAuthorization();

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddCors(options =>
{
    options.AddPolicy("DevCors", policy =>
    {
        policy
            .WithOrigins("http://localhost:5173", "http://localhost:5174")
            .AllowAnyHeader()
            .AllowAnyMethod()
            .AllowCredentials();   // Required for SignalR WebSocket from browser
    });
});

var app = builder.Build();

app.UseSwagger();

app.UseSwaggerUI(options =>
{
    options.SwaggerEndpoint("/core/openapi/v1.json",           "CoreService API");
    options.SwaggerEndpoint("/credit/swagger/v1/swagger.json", "CreditService API");
    options.SwaggerEndpoint("/user/swagger/v1/swagger.json",   "UserService API");
    options.SwaggerEndpoint("/settings/openapi/v1.json",       "SettingsService API");
    options.RoutePrefix = "swagger";
});

app.UseCors("DevCors");
app.UseAuthentication();   // validates JWT, populates HttpContext.User
app.UseAuthorization();
app.UseMiddleware<JwtForwardingMiddleware>();
app.MapReverseProxy();

Console.WriteLine($"Environment: {builder.Environment.EnvironmentName}");
app.Run();
