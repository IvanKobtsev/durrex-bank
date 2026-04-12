using Duende.IdentityServer.Models;
using Microsoft.AspNetCore.DataProtection;
using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.HttpOverrides;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Http.Resilience;
using MyApp.AuthService.Data;
using MyApp.AuthService.DTOs;
using MyApp.AuthService.Infrastructure;
using MyApp.AuthService.Middleware;
using MyApp.AuthService.Models;
using MyApp.AuthService.Services;
using StackExchange.Redis;

var builder = WebApplication.CreateBuilder(args);
var connectionString = builder.Configuration.GetConnectionString("DefaultConnection")!;

builder.Services.Configure<ForwardedHeadersOptions>(options =>
{
    options.ForwardedHeaders = ForwardedHeaders.XForwardedProto | ForwardedHeaders.XForwardedHost;
    options.KnownNetworks.Clear();
    options.KnownProxies.Clear();
});

builder.Services.AddDataProtection()
    .PersistKeysToFileSystem(
        new DirectoryInfo(Path.Combine(builder.Environment.ContentRootPath, "dp-keys"))
    )
    .SetApplicationName("MyApp.AuthService");

builder.Services.AddRazorPages();
builder.Services.AddControllers();

builder.Services.AddDbContext<AuthDbContext>(options => options.UseNpgsql(connectionString));

builder
    .Services.AddIdentity<ApplicationUser, IdentityRole<int>>(options =>
    {
        options.Password.RequireDigit = false;
        options.Password.RequiredLength = 6;
        options.Password.RequireNonAlphanumeric = false;
        options.Password.RequireUppercase = false;
    })
    .AddEntityFrameworkStores<AuthDbContext>()
    .AddDefaultTokenProviders();

builder.Services.AddHttpClient<UserServiceClient>(client =>
{
    client.BaseAddress = new Uri(builder.Configuration["Services:UserService"]!);
    client.DefaultRequestHeaders.Add(
        "X-Internal-Api-Key",
        builder.Configuration["InternalApiKey"]!
    );
})
.AddStandardResilienceHandler(options =>
{
    options.Retry.MaxRetryAttempts = 3;
    options.Retry.Delay = TimeSpan.FromMilliseconds(500);
    options.CircuitBreaker.FailureRatio = 0.7;
    options.CircuitBreaker.SamplingDuration = TimeSpan.FromSeconds(30);
    options.CircuitBreaker.MinimumThroughput = 5;
    options.CircuitBreaker.BreakDuration = TimeSpan.FromSeconds(30);
});

// Redis for distributed idempotency
builder.Services.AddSingleton<IConnectionMultiplexer>(
    ConnectionMultiplexer.Connect(
        builder.Configuration.GetConnectionString("Redis")
        ?? throw new InvalidOperationException("ConnectionStrings:Redis is not configured.")));

// MonitoringClient
builder.Services.AddHttpClient<MonitoringClient>(client =>
{
    client.BaseAddress = new Uri(builder.Configuration["Services:MonitoringService"]!);
    client.DefaultRequestHeaders.Add("X-Internal-Api-Key", builder.Configuration["InternalApiKey"]!);
});

var identityResources =
    builder
        .Configuration.GetSection("IdentityServer:IdentityResources")
        .Get<List<IdentityResource>>() ?? [];

var apiScopes =
    builder.Configuration.GetSection("IdentityServer:ApiScopes").Get<List<ApiScope>>() ?? [];

var apiResources =
    builder.Configuration.GetSection("IdentityServer:ApiResources").Get<List<ApiResource>>() ?? [];

var clients = builder.Configuration.GetSection("IdentityServer:Clients").Get<List<Client>>() ?? [];

builder
    .Services.AddIdentityServer(options =>
    {
        options.IssuerUri = builder.Configuration["IdentityServer:IssuerUri"];
        options.Events.RaiseErrorEvents = true;
        options.Events.RaiseFailureEvents = true;
        options.Events.RaiseSuccessEvents = true;
    })
    .AddInMemoryIdentityResources(identityResources)
    .AddInMemoryApiScopes(apiScopes)
    .AddInMemoryApiResources(apiResources)
    .AddInMemoryClients(clients)
    .AddAspNetIdentity<ApplicationUser>()
    .AddProfileService<HierarchicalProfileService>();

builder.Services.AddScoped<AuthSeeder>();

builder.Services.Configure<CookiePolicyOptions>(options =>
{
    options.MinimumSameSitePolicy = SameSiteMode.Unspecified;
    options.OnAppendCookie = cookieContext =>
        CheckSameSite(cookieContext.Context, cookieContext.CookieOptions);
    options.OnDeleteCookie = cookieContext =>
        CheckSameSite(cookieContext.Context, cookieContext.CookieOptions);
});

static void CheckSameSite(HttpContext httpContext, CookieOptions options)
{
    if (options.SameSite == SameSiteMode.None && !httpContext.Request.IsHttps)
    {
        options.SameSite = SameSiteMode.Unspecified;
    }
}

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AuthDbContext>();
    await db.Database.MigrateAsync();

    var seeder = scope.ServiceProvider.GetRequiredService<AuthSeeder>();
    await seeder.SeedAsync();
}

app.UseForwardedHeaders();
app.UsePathBase("/auth");

// Nginx strips the /services prefix before forwarding to the gateway,
// so we restore it in PathBase to ensure generated URLs (redirects, login, etc.)
// use the correct external path /services/auth.
if (!app.Environment.IsDevelopment())
    app.Use(
        (context, next) =>
        {
            context.Request.PathBase = new PathString("/services") + context.Request.PathBase;
            return next();
        }
    );

app.UseExceptionHandler(errApp =>
{
    errApp.Run(async context =>
    {
        var ex = context.Features.Get<IExceptionHandlerFeature>()?.Error;
        if (ex is null) return;

        // Only return JSON for /internal API paths
        if (!context.Request.Path.StartsWithSegments("/internal"))
            return;

        context.Response.StatusCode = StatusCodes.Status500InternalServerError;
        context.Response.ContentType = "application/json";
        await context.Response.WriteAsJsonAsync(new { error = "An unexpected error occurred." });

        try
        {
            var monitoring = context.RequestServices.GetService<MonitoringClient>();
            if (monitoring is not null)
                await monitoring.CaptureErrorAsync(new CaptureErrorEventDto
                {
                    Service = "AuthService",
                    Level = "Error",
                    Message = ex.Message,
                    ExceptionType = ex.GetType().FullName,
                    StackTrace = ex.StackTrace,
                    RequestMethod = context.Request.Method,
                    RequestPath = context.Request.Path,
                    OccurredAtUtc = DateTimeOffset.UtcNow,
                });
        }
        catch { }
    });
});

app.UseMiddleware<InternalApiKeyMiddleware>();
app.UseMiddleware<IdempotencyMiddleware>();
app.UseStaticFiles();
app.UseRouting();
app.UseIdentityServer();
app.UseCookiePolicy();
app.UseAuthorization();
app.MapRazorPages();
app.MapControllers();

app.Run();
