using System.Reflection;
using System.Text.Json.Serialization;
using Microsoft.AspNetCore.Diagnostics;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Http.Resilience;
using Microsoft.OpenApi;
using MyApp.UserService.Auth;
using MyApp.UserService.Data;
using MyApp.UserService.DTOs;
using MyApp.UserService.Infrastructure;
using MyApp.UserService.Middleware;
using MyApp.UserService.Repositories;
using MyApp.UserService.Services;
using MyApp.UserService.Swagger;
using StackExchange.Redis;

var builder = WebApplication.CreateBuilder(args);

builder
    .Services.AddControllers()
    .AddJsonOptions(options =>
        options.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter())
    );

builder.Services.AddSwaggerGen(options =>
{
    options.SwaggerDoc(
        "v1",
        new()
        {
            Title = "UserService API",
            Version = "v1",
            Description = "Manages user profiles, authentication and JWT issuance for Durrex Bank.",
        }
    );

    var xmlFile = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
    var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);
    options.IncludeXmlComments(xmlPath);

    options.UseInlineDefinitionsForEnums();

    options.OperationFilter<GatewayHeadersOperationFilter>();

    options.AddSecurityDefinition(
        "InternalApiKey",
        new OpenApiSecurityScheme
        {
            Type = SecuritySchemeType.ApiKey,
            In = ParameterLocation.Header,
            Name = "X-Internal-Api-Key",
            Description = "Internal API key (from appsettings InternalApiKey)",
        }
    );

    options.AddSecurityRequirement(document => new OpenApiSecurityRequirement
    {
        { new OpenApiSecuritySchemeReference("InternalApiKey", document), new List<string>() },
    });
});

builder.Services.AddDbContext<UserDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection"))
);
builder.Services.AddHttpContextAccessor();
builder.Services.AddScoped<ICurrentUserContext>(sp =>
{
    var http = sp.GetRequiredService<IHttpContextAccessor>().HttpContext;
    if (http is null)
        return new CurrentUserContext { Role = CallerRole.Internal };

    var userIdHeader  = http.Request.Headers["X-User-Id"].FirstOrDefault();
    var rolesHeader   = http.Request.Headers["X-User-Roles"].FirstOrDefault();

    if (string.IsNullOrEmpty(userIdHeader) || string.IsNullOrEmpty(rolesHeader))
        return new CurrentUserContext { Role = CallerRole.Internal };

    var roles = rolesHeader.Split(',', StringSplitOptions.RemoveEmptyEntries | StringSplitOptions.TrimEntries);
    var role = roles.Any(r => r.Equals("Employee", StringComparison.OrdinalIgnoreCase))
        ? CallerRole.Employee
        : CallerRole.Client;

    return new CurrentUserContext
    {
        UserId = int.TryParse(userIdHeader, out var id) ? id : null,
        Role   = role
    };
});

builder.Services.AddHttpClient<AuthServiceClient>(client =>
{
    client.BaseAddress = new Uri(builder.Configuration["Services:AuthService"]!);
    client.DefaultRequestHeaders.Add(
        "X-Internal-Api-Key",
        builder.Configuration["InternalApiKey"]!);
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

builder.Services.AddSingleton<IConnectionMultiplexer>(
    ConnectionMultiplexer.Connect(
        builder.Configuration.GetConnectionString("Redis")
        ?? throw new InvalidOperationException("ConnectionStrings:Redis is not configured.")));

builder.Services.AddHttpClient<MonitoringClient>(client =>
{
    client.BaseAddress = new Uri(builder.Configuration["Services:MonitoringService"]!);
    client.DefaultRequestHeaders.Add("X-Internal-Api-Key", builder.Configuration["InternalApiKey"]!);
});

builder.Services.AddScoped<DataSeeder>();
builder.Services.AddScoped<UserRegistrationService>();
builder.Services.AddScoped<IUserService, UserService>();
builder.Services.AddScoped<IUserRepository, UserRepository>();

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<UserDbContext>();
    await db.Database.MigrateAsync();

    var seeder = scope.ServiceProvider.GetRequiredService<DataSeeder>();
    await seeder.SeedAsync();
}

app.UseExceptionHandler(errApp =>
{
    errApp.Run(async context =>
    {
        var ex = context.Features.Get<IExceptionHandlerFeature>()?.Error;
        if (ex is null) return;

        var (status, message) = ex switch
        {
            KeyNotFoundException => (StatusCodes.Status404NotFound, ex.Message),
            InvalidOperationException => (StatusCodes.Status400BadRequest, ex.Message),
            UnauthorizedAccessException => (StatusCodes.Status403Forbidden, ex.Message),
            _ => (StatusCodes.Status500InternalServerError, "An unexpected error occurred.")
        };

        context.Response.StatusCode = status;
        context.Response.ContentType = "application/json";
        await context.Response.WriteAsJsonAsync(new { error = message });

        if (status >= 500)
        {
            try
            {
                var monitoring = context.RequestServices.GetService<MonitoringClient>();
                if (monitoring is not null)
                    await monitoring.CaptureErrorAsync(new CaptureErrorEventDto
                    {
                        Service = "UserService",
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
        }
    });
});

app.UseSwagger();
app.UseSwaggerUI(options =>
{
    options.SwaggerEndpoint("/swagger/v1/swagger.json", "UserService v1");
    options.RoutePrefix = "swagger";
});

if ((builder.Configuration["RandomFailureEnabled"]?.ToLower() ?? "false") == "true")
    app.UseMiddleware<RandomFailureMiddleware>();

app.UseMiddleware<IdempotencyMiddleware>();
app.UseMiddleware<InternalApiKeyMiddleware>();

app.MapControllers();

app.Run();
