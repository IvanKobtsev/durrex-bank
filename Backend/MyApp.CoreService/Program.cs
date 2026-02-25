using Microsoft.AspNetCore.Diagnostics;
using Microsoft.EntityFrameworkCore;
using MyApp.CoreService.Data;
using MyApp.CoreService.Middleware;
using Scalar.AspNetCore;

var builder = WebApplication.CreateBuilder(args);



builder.Services.AddControllers();
builder.Services.AddOpenApi();

builder.Services.AddDbContext<CoreDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

builder.Services.AddMediatR(cfg =>
    cfg.RegisterServicesFromAssembly(typeof(Program).Assembly));


var app = builder.Build();

// Auto-apply pending migrations on startup.
// In test environments, skip migrations and rely on EnsureCreated called by the test factory.
if (!app.Environment.IsEnvironment("Testing"))
{
    using var scope = app.Services.CreateScope();
    var db = scope.ServiceProvider.GetRequiredService<CoreDbContext>();
    await db.Database.MigrateAsync();
}

// Global exception handler: maps domain exceptions to HTTP status codes
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
            _ => (StatusCodes.Status500InternalServerError, "An unexpected error occurred.")
        };
        context.Response.StatusCode = status;
        context.Response.ContentType = "application/json";
        await context.Response.WriteAsJsonAsync(new { error = message });
    });
});

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
    app.MapScalarApiReference();
}

app.UseHttpsRedirection();

// Internal API key guard must be before routing
app.UseMiddleware<InternalApiKeyMiddleware>();

app.MapControllers();

app.Run();

// Exposes Program class to the test assembly
public partial class Program { }
