using Microsoft.AspNetCore.HttpOverrides;
using Microsoft.EntityFrameworkCore;
using MyApp.MonitoringService.Components;
using MyApp.MonitoringService.Data;
using MyApp.MonitoringService.DTOs;
using MyApp.MonitoringService.Infrastructure;
using MyApp.MonitoringService.Middleware;
using MyApp.MonitoringService.Services;

var builder = WebApplication.CreateBuilder(args);
var connectionString =
    builder.Configuration.GetConnectionString("Default")
    ?? throw new InvalidOperationException("ConnectionStrings:Default is not configured.");

builder.Services.Configure<ForwardedHeadersOptions>(options =>
{
    options.ForwardedHeaders = ForwardedHeaders.XForwardedProto | ForwardedHeaders.XForwardedHost;
    options.KnownNetworks.Clear();
    options.KnownProxies.Clear();
});

// Add services to the container.
builder.Services.AddRazorComponents().AddInteractiveServerComponents();
builder.Services.AddDbContextFactory<MonitoringDbContext>(options =>
    options.UseNpgsql(connectionString)
);
builder.Services.AddScoped<MonitoringEventService>();

var app = builder.Build();

app.UseForwardedHeaders();
app.UsePathBase("/monitoring");

// Nginx strips the /services prefix before forwarding to the gateway,
// so restore it to keep generated links and static asset URLs under /services/monitoring.
app.Use(
    (context, next) =>
    {
        context.Request.PathBase = new PathString("/services") + context.Request.PathBase;
        return next();
    }
);

await using (var scope = app.Services.CreateAsyncScope())
{
    var dbFactory = scope.ServiceProvider.GetRequiredService<
        IDbContextFactory<MonitoringDbContext>
    >();
    await using var db = await dbFactory.CreateDbContextAsync();
    await db.Database.MigrateAsync();
}

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Error", createScopeForErrors: true);
    // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
    app.UseHsts();
}

app.UseStatusCodePagesWithReExecute("/not-found", createScopeForStatusCodePages: true);
app.UseHttpsRedirection();

app.UseAntiforgery();

app.MapPost(
        "/api/events",
        async (
            CaptureErrorEventRequest request,
            MonitoringEventService monitoringEventService,
            CancellationToken cancellationToken
        ) =>
        {
            var capturedEvent = await monitoringEventService.CaptureAsync(
                request,
                cancellationToken
            );
            return Results.Created(
                $"/api/events/{capturedEvent.EventId}",
                new { capturedEvent.EventId, capturedEvent.ReceivedAtUtc }
            );
        }
    )
    .AddEndpointFilter<RequireInternalApiKeyEndpointFilter>()
    .WithName("CaptureErrorEvent");

app.MapGet(
        "/api/events",
        async (
            MonitoringEventService monitoringEventService,
            IConfiguration configuration,
            CancellationToken cancellationToken
        ) =>
        {
            var maxEvents = configuration.GetValue<int?>("Monitoring:MaxEventsOnDashboard") ?? 250;
            var events = await monitoringEventService.GetRecentEventsAsync(
                maxEvents,
                cancellationToken
            );
            return Results.Ok(events);
        }
    )
    .AddEndpointFilter<RequireInternalApiKeyEndpointFilter>()
    .WithName("GetErrorEvents");

app.MapGet(
        "/api/events/summary",
        async (
            MonitoringEventService monitoringEventService,
            IConfiguration configuration,
            CancellationToken cancellationToken
        ) =>
        {
            var maxEvents = configuration.GetValue<int?>("Monitoring:MaxEventsOnDashboard") ?? 250;
            var snapshot = await monitoringEventService.GetDashboardSnapshotAsync(
                maxEvents,
                cancellationToken
            );
            return Results.Ok(snapshot);
        }
    )
    .AddEndpointFilter<RequireInternalApiKeyEndpointFilter>()
    .WithName("GetErrorEventsSummary");

app.MapGet(
        "/api/requests",
        async (
            MonitoringEventService monitoringEventService,
            IConfiguration configuration,
            CancellationToken cancellationToken
        ) =>
        {
            var maxRequests =
                configuration.GetValue<int?>("Monitoring:MaxRequestsOnDashboard") ?? 250;
            var requests = await monitoringEventService.GetRecentRequestTracesAsync(
                maxRequests,
                cancellationToken
            );
            return Results.Ok(requests);
        }
    )
    .AddEndpointFilter<RequireInternalApiKeyEndpointFilter>()
    .WithName("GetRequestTraces");

app.MapPost(
        "/api/requests",
        async (
            CaptureRequestTraceRequest request,
            MonitoringEventService monitoringEventService,
            CancellationToken cancellationToken
        ) =>
        {
            await monitoringEventService.CaptureRequestTraceAsync(request, cancellationToken);
            return Results.Accepted();
        }
    )
    .AddEndpointFilter<RequireInternalApiKeyEndpointFilter>()
    .WithName("CaptureRequestTrace");

app.MapGet(
        "/api/requests/summary",
        async (
            MonitoringEventService monitoringEventService,
            IConfiguration configuration,
            CancellationToken cancellationToken
        ) =>
        {
            var maxRequests =
                configuration.GetValue<int?>("Monitoring:MaxRequestsOnDashboard") ?? 250;
            var snapshot = await monitoringEventService.GetRequestSnapshotAsync(
                maxRequests,
                cancellationToken
            );
            return Results.Ok(snapshot);
        }
    )
    .AddEndpointFilter<RequireInternalApiKeyEndpointFilter>()
    .WithName("GetRequestTracesSummary");

app.MapStaticAssets();
app.MapRazorComponents<App>().AddInteractiveServerRenderMode();

app.Run();
