using Microsoft.AspNetCore.Mvc;
using MyApp.CoreService.Services;

namespace MyApp.CoreService.Controllers;

/// <summary>
/// Example controller demonstrating Firebase notification usage.
/// This serves as a reference for implementing Firebase notifications in your endpoints.
/// </summary>
[ApiController]
[Route("api/[controller]")]
public class FirebaseExampleController(IFirebaseNotificationService firebaseService)
    : ControllerBase
{
    /// <summary>
    /// Example: Send notification to a specific user
    /// </summary>
    /// <param name="userId">The user ID to send notification to</param>
    /// <param name="title">Notification title</param>
    /// <param name="body">Notification body</param>
    /// <returns>Success response</returns>
    [HttpPost("send-to-user")]
    public async Task<IActionResult> SendToUser(
        [FromQuery] int userId,
        [FromQuery] string title,
        [FromQuery] string body,
        CancellationToken ct = default
    )
    {
        try
        {
            await firebaseService.SendToUserAsync(userId, title, body, ct: ct);
            return Ok(new { message = "Notification sent successfully" });
        }
        catch (Exception ex)
        {
            return BadRequest(new { error = ex.Message });
        }
    }

    /// <summary>
    /// Example: Send notification to all employees
    /// </summary>
    /// <param name="title">Notification title</param>
    /// <param name="body">Notification body</param>
    /// <returns>Success response</returns>
    [HttpPost("send-to-employees")]
    public async Task<IActionResult> SendToEmployees(
        [FromQuery] string title,
        [FromQuery] string body,
        CancellationToken ct = default
    )
    {
        try
        {
            await firebaseService.SendToAllEmployeesAsync(title, body, ct: ct);
            return Ok(new { message = "Notification sent to all employees" });
        }
        catch (Exception ex)
        {
            return BadRequest(new { error = ex.Message });
        }
    }

    /// <summary>
    /// Example: Send notification with custom data payload
    /// </summary>
    [HttpPost("send-with-data")]
    public async Task<IActionResult> SendWithData(
        [FromQuery] int userId,
        [FromQuery] string title,
        [FromQuery] string body,
        CancellationToken ct = default
    )
    {
        try
        {
            var customData = new Dictionary<string, string>
            {
                { "userId", userId.ToString() },
                { "timestamp", DateTime.UtcNow.ToString("O") },
                { "actionUrl", "https://your-app.com/notifications/details" },
            };

            await firebaseService.SendToUserAsync(userId, title, body, data: customData, ct: ct);

            return Ok(new { message = "Notification with data sent successfully" });
        }
        catch (Exception ex)
        {
            return BadRequest(new { error = ex.Message });
        }
    }

    /// <summary>
    /// Example: Transaction notification (real-world use case)
    /// </summary>
    [HttpPost("notify-transaction")]
    public async Task<IActionResult> NotifyTransaction(
        [FromQuery] int userId,
        [FromQuery] string transactionType,
        [FromQuery] decimal amount,
        [FromQuery] string currency,
        CancellationToken ct = default
    )
    {
        try
        {
            var title = $"Транзакция: {transactionType}";
            var body = $"Сумма: {amount} {currency}";

            var notificationData = new Dictionary<string, string>
            {
                { "transactionType", transactionType },
                { "amount", amount.ToString("F2") },
                { "currency", currency },
                { "timestamp", DateTime.UtcNow.ToString("O") },
                { "screen", "transactions" },
            };

            await firebaseService.SendToUserAsync(
                userId,
                title,
                body,
                data: notificationData,
                ct: ct
            );

            return Ok(new { message = "Transaction notification sent" });
        }
        catch (Exception ex)
        {
            return BadRequest(new { error = ex.Message });
        }
    }

    /// <summary>
    /// Example: Broadcast notification to all employees with data
    /// </summary>
    [HttpPost("broadcast-alert")]
    public async Task<IActionResult> BroadcastAlert(
        [FromQuery] string alertType,
        [FromQuery] string message,
        CancellationToken ct = default
    )
    {
        try
        {
            var notificationData = new Dictionary<string, string>
            {
                { "alertType", alertType },
                { "priority", "high" },
                { "timestamp", DateTime.UtcNow.ToString("O") },
                { "screen", "alerts" },
            };

            var title = $"System Alert: {alertType}";

            await firebaseService.SendToAllEmployeesAsync(
                title,
                message,
                data: notificationData,
                ct: ct
            );

            return Ok(new { message = "Alert broadcasted to all employees" });
        }
        catch (Exception ex)
        {
            return BadRequest(new { error = ex.Message });
        }
    }
}

/// <summary>
/// Usage Examples for the Firebase Notification Service
///
/// These examples show how to integrate Firebase notifications into your business logic.
/// </summary>
public static class FirebaseUsageExamples
{
    /// <summary>
    /// Example 1: In a business logic service
    /// </summary>
    public static async Task Example1_ServiceUsage(
        IFirebaseNotificationService firebaseService,
        CancellationToken ct
    )
    {
        // Send payment confirmation notification
        var userId = 123;
        var title = "Payment Confirmed";
        var body = "Your payment of $50.00 has been confirmed";
        var data = new Dictionary<string, string>
        {
            { "paymentId", "PAY-12345" },
            { "amount", "50.00" },
            { "currency", "USD" },
        };

        await firebaseService.SendToUserAsync(userId, title, body, data, ct);
    }

    /// <summary>
    /// Example 2: Batch notifications
    /// </summary>
    public static async Task Example2_BatchNotifications(
        IFirebaseNotificationService firebaseService,
        List<int> userIds,
        CancellationToken ct
    )
    {
        // Send notifications to multiple users
        var title = "Maintenance Window";
        var body = "System maintenance scheduled for tonight 2am-4am UTC";

        foreach (var userId in userIds)
        {
            await firebaseService.SendToUserAsync(userId, title, body, ct: ct);
        }
        // Note: These will be automatically batched internally by the service
    }

    /// <summary>
    /// Example 3: Employee alerts
    /// </summary>
    public static async Task Example3_EmployeeAlerts(
        IFirebaseNotificationService firebaseService,
        CancellationToken ct
    )
    {
        var title = "New High-Value Transaction";
        var body = "Transaction of $100,000 requires manual review";
        var data = new Dictionary<string, string>
        {
            { "transactionId", "TXN-67890" },
            { "amount", "100000" },
            { "actionRequired", "true" },
            { "reviewUrl", "https://admin.app/transactions/TXN-67890" },
        };

        await firebaseService.SendToAllEmployeesAsync(title, body, data, ct);
    }

    /// <summary>
    /// Example 4: Error notification handling
    /// </summary>
    public static async Task Example4_ErrorHandling(
        IFirebaseNotificationService firebaseService,
        CancellationToken ct
    )
    {
        try
        {
            var userId = 456;
            var title = "Account Alert";
            var body = "Unusual activity detected. Please verify your account.";

            await firebaseService.SendToUserAsync(userId, title, body, ct: ct);
        }
        catch (Exception ex) when (ex.Message.Contains("No Firebase tokens found"))
        {
            // User has no devices registered - this is normal
            Console.WriteLine("User has no active devices");
        }
        catch (Exception ex)
        {
            // Log other errors but don't crash the application
            Console.WriteLine($"Failed to send notification: {ex.Message}");
        }
    }
}
