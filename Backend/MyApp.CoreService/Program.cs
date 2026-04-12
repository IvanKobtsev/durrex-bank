using MassTransit;
using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using MyApp.CoreService.Auth;
using MyApp.CoreService.Data;
using MyApp.CoreService.ExchangeRates;
using MyApp.CoreService.Hubs;
using MyApp.CoreService.Infrastructure.Extensions;
using MyApp.CoreService.Messaging.Consumers;
using MyApp.CoreService.Messaging.Messages;
using MyApp.CoreService.Infrastructure;
using MyApp.CoreService.DTOs;
using MyApp.CoreService.Middleware;
using Scalar.AspNetCore;
using StackExchange.Redis;

var builder = WebApplication.CreateBuilder(args);

var isTesting = builder.Environment.IsEnvironment("Testing");

builder.Services.AddControllers();
builder.Services.AddOpenApi();

builder.Services.AddDbContext<CoreDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection"))
);

builder.Services.AddMediatR(cfg => cfg.RegisterServicesFromAssembly(typeof(Program).Assembly));

builder.Services.AddHttpContextAccessor();
builder.Services.AddScoped<ICurrentUserContext>(sp =>
{
    var http = sp.GetRequiredService<IHttpContextAccessor>().HttpContext;
    if (http is null)
        return new CurrentUserContext { Role = CallerRole.Internal };

    var userIdHeader = http.Request.Headers["X-User-Id"].FirstOrDefault();
    var roleHeader = http.Request.Headers["X-User-Roles"].FirstOrDefault();

    if (string.IsNullOrEmpty(userIdHeader) || string.IsNullOrEmpty(roleHeader))
        return new CurrentUserContext { Role = CallerRole.Internal };

    var role = roleHeader.Contains("Employee", StringComparison.OrdinalIgnoreCase)
        ? CallerRole.Employee
        : CallerRole.Client;

    return new CurrentUserContext
    {
        UserId = int.TryParse(userIdHeader, out var id) ? id : null,
        Role = role,
    };
});

builder.Services.AddMemoryCache();

builder.Services.AddSingleton<IConnectionMultiplexer>(
    ConnectionMultiplexer.Connect(
        builder.Configuration.GetConnectionString("Redis")
        ?? throw new InvalidOperationException("ConnectionStrings:Redis is not configured.")));

builder.Services.AddHttpClient<MonitoringClient>(client =>
{
    client.BaseAddress = new Uri(builder.Configuration["Services:MonitoringService"]!);
    client.DefaultRequestHeaders.Add("X-Internal-Api-Key", builder.Configuration["InternalApiKey"]!);
});

if (isTesting)
    builder.Services.AddSingleton<IExchangeRateService, NoOpExchangeRateService>();
else
    builder.Services.AddHttpClient<IExchangeRateService, ExchangeRateService>();

if (!isTesting)
    builder.Services.AddHostedService<ExchangeRateRefresher>();

builder.Services.AddSignalR();
builder.Services.AddSingleton<IUserIdProvider, HeaderUserIdProvider>();

// Add Firebase services with modern configuration
if (!isTesting)
    builder.Services.AddFirebaseServices(builder.Configuration);

builder.Services.AddMassTransit(x =>
{
    if (!isTesting)
    {
        x.AddEntityFrameworkOutbox<CoreDbContext>(o =>
        {
            o.UsePostgres();
            o.UseBusOutbox();
        });
    }

    x.AddConsumer<TransactionRequestedConsumer>();

    x.AddRequestClient<TransactionRequested>();

    if (isTesting)
    {
        x.UsingInMemory(
            (context, cfg) =>
            {
                cfg.ConfigureEndpoints(context);
            }
        );
    }
    else
    {
        x.UsingRabbitMq(
            (ctx, cfg) =>
            {
                var host = builder.Configuration["RabbitMQ:Host"] ?? "localhost";
                var vhost = builder.Configuration["RabbitMQ:VirtualHost"] ?? "/";
                var user = builder.Configuration["RabbitMQ:Username"] ?? "guest";
                var pass = builder.Configuration["RabbitMQ:Password"] ?? "guest";

                cfg.Host(
                    host,
                    vhost,
                    h =>
                    {
                        h.Username(user);
                        h.Password(pass);
                    }
                );

                cfg.UseMessageRetry(r =>
                    r.Intervals(
                        TimeSpan.FromSeconds(1),
                        TimeSpan.FromSeconds(5),
                        TimeSpan.FromSeconds(15)
                    )
                );

                cfg.ConfigureEndpoints(ctx);
            }
        );
    }
});

var app = builder.Build();

if (!isTesting)
{
    using var scope = app.Services.CreateScope();
    var db = scope.ServiceProvider.GetRequiredService<CoreDbContext>();
    var cfg = scope.ServiceProvider.GetRequiredService<IConfiguration>();
    await db.Database.MigrateAsync();
    await MasterAccountSeeder.SeedAsync(db, cfg);
}

app.UseExceptionHandler(errApp =>
{
    errApp.Run(async context =>
    {
        var ex = context.Features.Get<IExceptionHandlerFeature>()?.Error;
        var (status, message) = ex switch
        {
            KeyNotFoundException => (StatusCodes.Status404NotFound, ex.Message),
            InvalidOperationException => (StatusCodes.Status400BadRequest, ex.Message),
            ArgumentException => (StatusCodes.Status400BadRequest, ex.Message),
            RequestFaultException rex => MapRequestFault(rex),
            _ => (StatusCodes.Status500InternalServerError, "An unexpected error occurred."),
        };
        context.Response.StatusCode = status;
        context.Response.ContentType = "application/json";
        await context.Response.WriteAsJsonAsync(new { error = message });

        if (status >= 500 && ex is not null)
        {
            try
            {
                var monitoring = context.RequestServices.GetService<MonitoringClient>();
                if (monitoring is not null)
                    await monitoring.CaptureErrorAsync(new CaptureErrorEventDto
                    {
                        Service = "CoreService",
                        Level = "Error",
                        Message = ex.Message,
                        ExceptionType = ex.GetType().FullName,
                        StackTrace = ex.StackTrace,
                        RequestMethod = context.Request.Method,
                        RequestPath = context.Request.Path,
                        OccurredAtUtc = DateTimeOffset.UtcNow,
                    });
            }
            catch { /* monitoring errors must not affect the response */ }
        }
    });
});

static (int status, string message) MapRequestFault(RequestFaultException rex)
{
    if (rex.InnerException is KeyNotFoundException kfe)
        return (StatusCodes.Status404NotFound, kfe.Message);
    if (rex.InnerException is InvalidOperationException ioe)
        return (StatusCodes.Status400BadRequest, ioe.Message);
    if (rex.InnerException is ArgumentException ae)
        return (StatusCodes.Status400BadRequest, ae.Message);

    var faultEx = rex.Fault?.Exceptions?.FirstOrDefault();
    if (faultEx is not null && faultEx.ExceptionType is { } type)
    {
        if (type.Contains("KeyNotFoundException", StringComparison.Ordinal))
            return (StatusCodes.Status404NotFound, faultEx.Message);
        if (type.Contains("InvalidOperationException", StringComparison.Ordinal))
            return (StatusCodes.Status400BadRequest, faultEx.Message);
        if (type.Contains("ArgumentException", StringComparison.Ordinal))
            return (StatusCodes.Status400BadRequest, faultEx.Message);
    }

    return (StatusCodes.Status400BadRequest, rex.Message);
}

app.MapOpenApi();
app.MapScalarApiReference();

app.UseHttpsRedirection();

app.UseMiddleware<RandomFailureMiddleware>();
app.UseMiddleware<IdempotencyMiddleware>();
app.UseMiddleware<InternalApiKeyMiddleware>();

app.MapHub<TransactionHub>("/hubs/transactions");
app.MapControllers();

app.Run();

public partial class Program { }
