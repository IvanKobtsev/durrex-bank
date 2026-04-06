using System.Reflection;
using MassTransit;
using Microsoft.AspNetCore.Diagnostics;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi.Models;
using MyApp.CreditService.Auth;
using MyApp.CreditService.Middleware;
using MyApp.CreditService.Services;
using MyApp.CreditService.Swagger;
using Npgsql;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddMediatR(cfg => cfg.RegisterServicesFromAssembly(typeof(Program).Assembly));

var rawConnectionString =
    builder.Configuration.GetConnectionString("Default")
    ?? builder.Configuration.GetConnectionString("DefaultConnection")
    ?? throw new InvalidOperationException(
        "Connection string 'Default' or 'DefaultConnection' is required."
    );

var connectionStringBuilder = new NpgsqlConnectionStringBuilder(rawConnectionString);

builder.Services.AddDbContext<CreditDbContext>(options =>
    options.UseNpgsql(
        connectionStringBuilder.ConnectionString,
        npgsql => npgsql.EnableRetryOnFailure(5, TimeSpan.FromSeconds(5), null)
    )
);

builder.Services.AddMassTransit(x =>
{
    x.AddEntityFrameworkOutbox<CreditDbContext>(o =>
    {
        o.UsePostgres();
        o.UseBusOutbox();
    });

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

            cfg.ConfigureEndpoints(ctx);
        }
    );
});

builder.Services.AddHostedService<PaymentSchedulerService>();

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

builder.Services.AddSwaggerGen(options =>
{
    options.SwaggerDoc(
        "v1",
        new OpenApiInfo
        {
            Title = "CreditService API",
            Version = "v1",
            Description =
                "Manages credit tariffs, loan issuance and repayment scheduling for Durrex Bank.",
        }
    );

    var xmlFile = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
    var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);
    options.IncludeXmlComments(xmlPath);

    options.OperationFilter<GatewayHeadersOperationFilter>();

    options.AddSecurityDefinition(
        "InternalApiKey",
        new OpenApiSecurityScheme
        {
            Type = SecuritySchemeType.ApiKey,
            In = ParameterLocation.Header,
            Name = "X-Internal-Api-Key",
            Description =
                "Internal API key required for all endpoints (from appsettings InternalApiKey).",
        }
    );

    options.AddSecurityRequirement(
        new OpenApiSecurityRequirement
        {
            {
                new OpenApiSecurityScheme
                {
                    Reference = new OpenApiReference
                    {
                        Type = ReferenceType.SecurityScheme,
                        Id = "InternalApiKey",
                    },
                },
                Array.Empty<string>()
            },
        }
    );
});

var app = builder.Build();
await ApplyMigrationsWithRetryAsync(app.Services, app.Logger);

app.UseExceptionHandler(exceptionApp =>
    exceptionApp.Run(async context =>
    {
        var ex = context.Features.Get<IExceptionHandlerFeature>()?.Error;

        var (status, message) = ex switch
        {
            KeyNotFoundException => (StatusCodes.Status404NotFound, ex.Message),
            InvalidOperationException => (StatusCodes.Status400BadRequest, ex.Message),
            UnauthorizedAccessException => (StatusCodes.Status403Forbidden, ex.Message),
            HttpRequestException httpEx => (
                (int)(httpEx.StatusCode ?? System.Net.HttpStatusCode.BadGateway),
                httpEx.Message
            ),
            _ => (StatusCodes.Status500InternalServerError, "An unexpected error occurred."),
        };

        context.Response.StatusCode = status;
        context.Response.ContentType = "application/json";
        await context.Response.WriteAsJsonAsync(new { error = message });
    })
);

app.UseSwagger();
app.UseSwaggerUI(options =>
{
    options.SwaggerEndpoint("/swagger/v1/swagger.json", "CreditService v1");
    options.RoutePrefix = "swagger";
});

app.UseMiddleware<InternalApiKeyMiddleware>();

app.MapControllers();

app.Run();

static async Task ApplyMigrationsWithRetryAsync(IServiceProvider services, ILogger logger)
{
    const int maxAttempts = 8;

    for (var attempt = 1; attempt <= maxAttempts; attempt++)
    {
        try
        {
            using var scope = services.CreateScope();
            var db = scope.ServiceProvider.GetRequiredService<CreditDbContext>();
            await db.Database.MigrateAsync();
            return;
        }
        catch (Exception ex) when (attempt < maxAttempts)
        {
            var delay = TimeSpan.FromSeconds(Math.Min(20, attempt * 3));
            logger.LogWarning(
                ex,
                "Database migration attempt {Attempt}/{MaxAttempts} failed. Retrying in {DelaySeconds}s.",
                attempt,
                maxAttempts,
                delay.TotalSeconds
            );
            await Task.Delay(delay);
        }
    }

    using var finalScope = services.CreateScope();
    var finalDb = finalScope.ServiceProvider.GetRequiredService<CreditDbContext>();
    await finalDb.Database.MigrateAsync();
}
