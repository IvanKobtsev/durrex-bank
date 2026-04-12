using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.HttpOverrides;
using Microsoft.IdentityModel.Tokens;
using MyApp.Gateway;
using MyApp.Gateway.Middlewares;
using Yarp.ReverseProxy.Transforms;

var builder = WebApplication.CreateBuilder(args);

builder.Logging.AddConsole();

var monitoringServiceBaseAddress =
    builder.Configuration["Monitoring:ServiceBaseUrl"]
    ?? builder.Configuration[
        "ReverseProxy:Clusters:monitoringCluster:Destinations:monitoringDestination:Address"
    ]
    ?? throw new InvalidOperationException(
        "Monitoring service base address is not configured. Set Monitoring:ServiceBaseUrl or the monitoring reverse proxy destination."
    );

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
            options.Audience = builder.Configuration["Oidc:Audience"];
            options.RequireHttpsMetadata = false;
            options.MapInboundClaims = false;

            options.TokenValidationParameters = new TokenValidationParameters
            {
                ValidateIssuer = true,
                ValidateAudience = true,
                ValidAudiences = [builder.Configuration["Oidc:Audience"]!],
                ValidateLifetime = true,
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

builder.Services.AddHttpClient(
    RequestTracingMiddleware.MonitoringClientName,
    client =>
    {
        client.BaseAddress = new Uri(monitoringServiceBaseAddress, UriKind.Absolute);
        client.DefaultRequestHeaders.Add(
            "X-Internal-Api-Key",
            builder.Configuration["InternalApiKey"]
                ?? throw new InvalidOperationException("InternalApiKey is not configured.")
        );
    }
);

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddCors(options =>
{
    options.AddPolicy(
        "ProdCors",
        policy =>
        {
            policy
                .WithOrigins("https://swagor-time.ru")
                .AllowAnyHeader()
                .AllowAnyMethod()
                .AllowCredentials();
        }
    );

    options.AddPolicy(
        "DevCors",
        policy =>
        {
            policy
                .WithOrigins("http://localhost:5173")
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
    options.SwaggerEndpoint(
        (!app.Environment.IsDevelopment() ? "/services" : "") + "/core/openapi/v1.json",
        "CoreService API"
    );
    options.SwaggerEndpoint(
        (!app.Environment.IsDevelopment() ? "/services" : "") + "/credit/swagger/v1/swagger.json",
        "CreditService API"
    );
    options.SwaggerEndpoint(
        (!app.Environment.IsDevelopment() ? "/services" : "") + "/user/swagger/v1/swagger.json",
        "UserService API"
    );
    options.SwaggerEndpoint(
        (!app.Environment.IsDevelopment() ? "/services" : "") + "/web-app-settings/openapi/v1.json",
        "Web App Settings Service API"
    );
    options.SwaggerEndpoint(
        (!app.Environment.IsDevelopment()
            ? "/services"
            : "") + "/mobile-app-settings/openapi/v1.json",
        "Mobile App Settings Service API"
    );
    options.RoutePrefix = "swagger";
});

app.UseForwardedHeaders();
app.UseMiddleware<RequestTracingMiddleware>();
app.UseCors(!app.Environment.IsDevelopment() ? "ProdCors" : "DevCors");
app.UseAuthentication();
app.UseAuthorization();
app.UseMiddleware<JwtForwardingMiddleware>();
app.UseWebSockets();
app.MapReverseProxy();

Console.WriteLine($"Environment: {builder.Environment.EnvironmentName}");
app.Run();
