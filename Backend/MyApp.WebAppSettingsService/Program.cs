using Microsoft.AspNetCore.Diagnostics;
using Microsoft.EntityFrameworkCore;
using MyApp.WebAppSettingsService.Auth;
using MyApp.WebAppSettingsService.Data;
using MyApp.WebAppSettingsService.Middleware;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddOpenApi();

builder.Services.AddDbContext<SettingsDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("Default"))
);

builder.Services.AddHttpContextAccessor();
builder.Services.AddScoped<ICurrentUserContext>(sp =>
{
    var http = sp.GetRequiredService<IHttpContextAccessor>().HttpContext;
    if (http is null)
        return new CurrentUserContext();

    var userIdHeader = http.Request.Headers["X-User-Id"].FirstOrDefault();
    var rolesHeader = http.Request.Headers["X-User-Roles"].FirstOrDefault();

    var roles = string.IsNullOrEmpty(rolesHeader)
        ? (IReadOnlySet<string>)new HashSet<string>(StringComparer.OrdinalIgnoreCase)
        : rolesHeader
            .Split(',', StringSplitOptions.RemoveEmptyEntries | StringSplitOptions.TrimEntries)
            .ToHashSet(StringComparer.OrdinalIgnoreCase);

    return new CurrentUserContext
    {
        UserId = int.TryParse(userIdHeader, out var id) ? id : null,
        Roles = roles,
    };
});

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<SettingsDbContext>();
    await db.Database.MigrateAsync();
}

app.UseExceptionHandler(errApp =>
    errApp.Run(async context =>
    {
        var ex = context.Features.Get<IExceptionHandlerFeature>()?.Error;
        var (status, message) = ex switch
        {
            KeyNotFoundException => (404, ex.Message),
            InvalidOperationException => (400, ex.Message),
            ArgumentException => (400, ex.Message),
            _ => (500, "An unexpected error occurred."),
        };
        context.Response.StatusCode = status;
        await context.Response.WriteAsJsonAsync(new { error = message });
    })
);

if (app.Environment.IsDevelopment())
    app.MapOpenApi();

app.UseMiddleware<InternalApiKeyMiddleware>();
app.MapControllers();
app.Run();
