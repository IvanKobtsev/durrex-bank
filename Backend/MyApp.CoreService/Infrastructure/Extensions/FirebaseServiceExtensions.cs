using FirebaseAdmin;
using Google.Apis.Auth.OAuth2;
using MyApp.CoreService.Infrastructure.Options;
using MyApp.CoreService.Services;

namespace MyApp.CoreService.Infrastructure.Extensions;

/// <summary>
/// Extension methods for registering Firebase services in the dependency injection container.
/// </summary>
public static class FirebaseServiceExtensions
{
    /// <summary>
    /// Adds Firebase services to the dependency injection container.
    /// This includes initializing the Firebase Admin SDK and registering the notification service.
    /// </summary>
    /// <param name="services">The service collection.</param>
    /// <param name="configuration">The application configuration.</param>
    /// <returns>The service collection for method chaining.</returns>
    /// <exception cref="InvalidOperationException">
    /// Thrown when Firebase:CredentialsPath is not configured or the credentials file cannot be accessed.
    /// </exception>
    public static IServiceCollection AddFirebaseServices(
        this IServiceCollection services,
        IConfiguration configuration
    )
    {
        // Bind configuration to options object
        services.Configure<FirebaseOptions>(configuration.GetSection(FirebaseOptions.SectionName));

        var firebaseOptions = new FirebaseOptions();
        configuration.GetSection(FirebaseOptions.SectionName).Bind(firebaseOptions);

        // Validate configuration
        ValidateFirebaseConfiguration(firebaseOptions);

        // Initialize Firebase Admin SDK
        InitializeFirebaseApp(firebaseOptions);

        // Register services
        services.AddScoped<IFirebaseNotificationService, FirebaseNotificationService>();

        return services;
    }

    /// <summary>
    /// Validates the Firebase configuration settings.
    /// </summary>
    /// <param name="options">The Firebase options to validate.</param>
    /// <exception cref="InvalidOperationException">
    /// Thrown when required configuration is missing or invalid.
    /// </exception>
    private static void ValidateFirebaseConfiguration(FirebaseOptions options)
    {
        if (string.IsNullOrWhiteSpace(options.CredentialsPath))
        {
            throw new InvalidOperationException(
                "Firebase credentials path is not configured. "
                    + "Please set 'Firebase:CredentialsPath' in appsettings.json or environment variables. "
                    + "Example: \"Firebase:CredentialsPath\": \"firebase-credentials.json\""
            );
        }

        // Resolve the credentials path (handle relative paths)
        var credentialsPath = ResolveCredentialsPath(options.CredentialsPath);

        if (!File.Exists(credentialsPath))
        {
            throw new InvalidOperationException(
                $"Firebase credentials file not found at path: {credentialsPath}. "
                    + $"Original configured path: {options.CredentialsPath}"
            );
        }

        if (options.BatchSize <= 0 || options.BatchSize > 500)
        {
            throw new InvalidOperationException(
                $"Firebase BatchSize must be between 1 and 500. Current value: {options.BatchSize}"
            );
        }

        if (options.SendTimeout <= 0)
        {
            throw new InvalidOperationException(
                $"Firebase SendTimeout must be greater than 0. Current value: {options.SendTimeout}"
            );
        }
    }

    /// <summary>
    /// Initializes the Firebase Admin SDK with the provided credentials.
    /// </summary>
    /// <param name="options">The Firebase options containing credentials path.</param>
    /// <exception cref="InvalidOperationException">
    /// Thrown when Firebase initialization fails.
    /// </exception>
    private static void InitializeFirebaseApp(FirebaseOptions options)
    {
        try
        {
            // Check if Firebase is already initialized
            if (FirebaseApp.DefaultInstance != null)
            {
                return; // Already initialized, skip
            }

            var credentialsPath = ResolveCredentialsPath(options.CredentialsPath!);

            var appOptions = new AppOptions
            {
                Credential = CredentialFactory
                    .FromFile<ServiceAccountCredential>(credentialsPath)
                    .ToGoogleCredential(),
            };

            if (!string.IsNullOrWhiteSpace(options.ProjectId))
            {
                appOptions.ProjectId = options.ProjectId;
            }

            FirebaseApp.Create(appOptions);
        }
        catch (Exception ex) when (!(ex is InvalidOperationException))
        {
            throw new InvalidOperationException(
                $"Failed to initialize Firebase Admin SDK: {ex.Message}",
                ex
            );
        }
    }

    /// <summary>
    /// Resolves the credentials file path, handling both absolute and relative paths.
    /// </summary>
    /// <param name="credentialsPath">The configured credentials path.</param>
    /// <returns>The resolved absolute path to the credentials file.</returns>
    private static string ResolveCredentialsPath(string credentialsPath)
    {
        // If path is absolute, return it as-is
        if (Path.IsPathRooted(credentialsPath))
        {
            return credentialsPath;
        }

        // If path is relative, resolve it relative to the application base directory
        var basePath = AppContext.BaseDirectory;
        return Path.Combine(basePath, credentialsPath);
    }
}
