using Microsoft.AspNetCore.SignalR;

namespace MyApp.MonitoringService.Hubs;

/// <summary>
/// SignalR hub used to push real-time notifications to connected dashboard pages.
/// The hub itself is thin – it only serves as an endpoint; all broadcasts are
/// initiated server-side via <see cref="IHubContext{MonitoringHub}"/>.
/// </summary>
public sealed class MonitoringHub : Hub
{
    /// <summary>Method name broadcast when a new error event is captured.</summary>
    public const string EventCaptured = "EventCaptured";

    /// <summary>Method name broadcast when a new request trace is captured.</summary>
    public const string RequestTraceCaptured = "RequestTraceCaptured";
}
