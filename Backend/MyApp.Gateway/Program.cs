using Microsoft.AspNetCore.HttpOverrides;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using MyApp.Gateway;
using MyApp.Gateway.Middlewares;
using Yarp.ReverseProxy.Transforms;

var builder = WebApplication.CreateBuilder(args);

builder.Logging.AddConsole();

builder.Services.Configure<ForwardedHeadersOptions>(options =>
{
    options.ForwardedHeaders = ForwardedHeaders.XForwardedProto | ForwardedHeaders.XForwardedHost;
    options.KnownNetworks.Clear();
    options.KnownProxies.Clear();
});

builder
    .Services.AddReverseProxy()
    .LoadFromConfig(builder.Configuration.GetSection("ReverseProxy"))
    .AddTransforms(builderContext =>
    {
        builderContext.AddRequestTransform(transformContext =>
        {
            if (
                !transformContext.HttpContext.Request.Path.StartsWithSegments(
                    "/auth",
                    StringComparison.OrdinalIgnoreCase
                )
            )
            {
                transformContext.ProxyRequest.Headers.TryAddWithoutValidation(
                    "X-Internal-Api-Key",
                    builder.Configuration["InternalApiKey"]
                );
            }
            return ValueTask.CompletedTask;
        });

    });

builder
    .Services.AddAuthentication("Bearer")
    .AddJwtBearer(
        "Bearer",
        options =>
        {
            options.Authority = builder.Configuration["Oidc:Authority"];
            options.RequireHttpsMetadata = false;
            options.MapInboundClaims = false;

            options.TokenValidationParameters = new TokenValidationParameters
            {
                ValidateIssuer = false,
                ValidateAudience = false,
                ValidAudiences = [builder.Configuration["Oidc:Audience"]!],
                ValidateLifetime = false,
                ClockSkew = TimeSpan.FromSeconds(30),
                NameClaimType = "sub",
                RoleClaimType = "role",
            };

            options.Events = new JwtBearerEvents
            {
                OnMessageReceived = context =>
                {
                    var accessToken = context.Request.Query["access_token"];
                    var path = context.HttpContext.Request.Path;
                    if (!string.IsNullOrEmpty(accessToken) && path.StartsWithSegments("/core/hubs"))
                    {
                        context.Token = accessToken;
                    }
                    return Task.CompletedTask;
                },
            };
        }
    );

builder.Services.AddAuthorization();

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddCors(options =>
{
    options.AddPolicy(
        "DevCors",
        policy =>
        {
            policy
                .WithOrigins("https://swagor-time.ru")
                .AllowAnyHeader()
                .AllowAnyMethod()
                .AllowCredentials();
        }
    );
});

var app = builder.Build();

app.UseSwagger();

app.UseSwaggerUI(options =>
{
    options.SwaggerEndpoint("/services/core/openapi/v1.json", "CoreService API");
    options.SwaggerEndpoint("/services/credit/swagger/v1/swagger.json", "CreditService API");
    options.SwaggerEndpoint("/services/user/swagger/v1/swagger.json", "UserService API");
    options.SwaggerEndpoint("/services/web-app-settings/openapi/v1.json", "Web App Settings Service API");
    options.SwaggerEndpoint(
        "/services/mobile-app-settings/openapi/v1.json",
        "Mobile App Settings Service API"
    );
    options.RoutePrefix = "swagger";
});

app.UseForwardedHeaders();
app.UseCors("DevCors");
app.UseAuthentication();
app.UseAuthorization();
app.UseMiddleware<JwtForwardingMiddleware>();
app.UseWebSockets();
app.MapReverseProxy();

Console.WriteLine($"Environment: {builder.Environment.EnvironmentName}");
app.Run();
