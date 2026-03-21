using Microsoft.AspNetCore.SignalR;

namespace MyApp.CoreService.Hubs;

public class HeaderUserIdProvider : IUserIdProvider
{
    public string? GetUserId(HubConnectionContext connection) =>
        connection.GetHttpContext()?.Request.Headers["X-User-Id"].FirstOrDefault();
}
