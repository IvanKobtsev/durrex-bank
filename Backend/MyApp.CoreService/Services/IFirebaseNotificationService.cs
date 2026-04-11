namespace MyApp.CoreService.Services;

public interface IFirebaseNotificationService
{
    Task SendToUserAsync(
        int userId,
        string title,
        string body,
        Dictionary<string, string>? data = null,
        CancellationToken ct = default
    );

    Task SendToAllEmployeesAsync(
        string title,
        string body,
        Dictionary<string, string>? data = null,
        CancellationToken ct = default
    );
}
