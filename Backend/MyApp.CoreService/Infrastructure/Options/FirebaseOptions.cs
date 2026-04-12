namespace MyApp.CoreService.Infrastructure.Options;

/// <summary>
/// Firebase configuration options for ASP.NET Core dependency injection.
/// </summary>
public class FirebaseOptions
{
    /// <summary>
    /// Configuration section name in appsettings.json
    /// </summary>
    public const string SectionName = "Firebase";

    /// <summary>
    /// Path to Firebase service account JSON credentials file.
    /// Can be an absolute path or relative to the application root.
    /// </summary>
    public string? CredentialsPath { get; set; }

    /// <summary>
    /// Firebase project ID (optional, for logging/debugging purposes).
    /// </summary>
    public string? ProjectId { get; set; }

    /// <summary>
    /// Public base URL of the web frontend used for Firebase Web Push click targets.
    /// Example: https://swagor-time.ru or http://localhost:5173
    /// </summary>
    public string? FrontendBaseUrl { get; set; }

    /// <summary>
    /// Batch size for multicast Firebase Cloud Messaging operations.
    /// Firebase limits multicast messages to 500 tokens per request.
    /// Default: 500
    /// </summary>
    public int BatchSize { get; set; } = 500;

    /// <summary>
    /// Timeout in seconds for Firebase messaging operations.
    /// Default: 30 seconds
    /// </summary>
    public int SendTimeout { get; set; } = 30;

    /// <summary>
    /// Whether to validate Firebase credentials on application startup.
    /// Default: true
    /// </summary>
    public bool ValidateOnStartup { get; set; } = true;
}
