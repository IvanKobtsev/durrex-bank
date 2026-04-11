using FirebaseAdmin.Messaging;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using MyApp.CoreService.Auth;
using MyApp.CoreService.Data;
using MyApp.CoreService.Infrastructure.Options;

namespace MyApp.CoreService.Services;

public sealed class FirebaseNotificationService(
    CoreDbContext db,
    IOptions<FirebaseOptions> firebaseOptions,
    ILogger<FirebaseNotificationService> logger
) : IFirebaseNotificationService
{
    private readonly FirebaseOptions _options = firebaseOptions.Value;

    public async Task SendToUserAsync(
        int userId,
        string title,
        string body,
        Dictionary<string, string>? data = null,
        CancellationToken ct = default
    )
    {
        var tokens = await db
            .FirebaseTokens.AsNoTracking()
            .Where(token => token.UserId == userId)
            .Select(token => token.Token)
            .Distinct()
            .ToListAsync(ct);

        if (tokens.Count == 0)
        {
            logger.LogWarning("No Firebase tokens found for user {UserId}", userId);
            return;
        }

        await SendToTokensAsync(tokens, title, body, data, $"user {userId}", ct);
    }

    public async Task SendToAllEmployeesAsync(
        string title,
        string body,
        Dictionary<string, string>? data = null,
        CancellationToken ct = default
    )
    {
        var tokens = await db
            .FirebaseTokens.AsNoTracking()
            .Where(token => token.Role == CallerRole.Employee)
            .Select(token => token.Token)
            .Distinct()
            .ToListAsync(ct);

        if (tokens.Count == 0)
        {
            logger.LogWarning("No Firebase tokens found for employees");
            return;
        }

        await SendToTokensAsync(tokens, title, body, data, "all employees", ct);
    }

    private async Task SendToTokensAsync(
        IReadOnlyCollection<string> tokens,
        string title,
        string body,
        Dictionary<string, string>? data,
        string audience,
        CancellationToken ct
    )
    {
        var sanitizedTokens = tokens
            .Where(token => !string.IsNullOrWhiteSpace(token))
            .Distinct(StringComparer.Ordinal)
            .ToArray();

        if (sanitizedTokens.Length == 0)
        {
            logger.LogWarning(
                "Skipping Firebase notification send because audience {Audience} has no valid tokens",
                audience
            );
            return;
        }

        foreach (var batch in sanitizedTokens.Chunk(_options.BatchSize))
        {
            using var timeoutCts = CancellationTokenSource.CreateLinkedTokenSource(ct);
            timeoutCts.CancelAfter(TimeSpan.FromSeconds(_options.SendTimeout));

            var payloadData = BuildPayloadData(title, body, data);
            var clickLink = ResolveClickLink(payloadData, _options.FrontendBaseUrl);

            var message = new MulticastMessage
            {
                Tokens = batch,
                Notification = new Notification { Title = title, Body = body },
                Data = payloadData,
                Webpush = new WebpushConfig
                {
                    Notification = new WebpushNotification { Title = title, Body = body },
                    FcmOptions = clickLink is null ? null : new WebpushFcmOptions { Link = clickLink },
                },
            };

            try
            {
                var response = await FirebaseMessaging
                    .DefaultInstance.SendEachForMulticastAsync(message, timeoutCts.Token);

                logger.LogInformation(
                    "Sent Firebase notification to {Audience}: {SuccessCount} succeeded, {FailureCount} failed in batch of {BatchSize}",
                    audience,
                    response.SuccessCount,
                    response.FailureCount,
                    batch.Length
                );

                if (response.FailureCount > 0)
                {
                    var invalidTokens = GetInvalidTokens(batch, response);
                    if (invalidTokens.Count > 0)
                        await RemoveInvalidTokensAsync(invalidTokens, timeoutCts.Token);
                }
            }
            catch (FirebaseMessagingException ex)
            {
                logger.LogError(
                    ex,
                    "Firebase send failed for {Audience}. MessagingErrorCode: {MessagingErrorCode}",
                    audience,
                    ex.MessagingErrorCode
                );
                throw;
            }
        }
    }

    private static List<string> GetInvalidTokens(string[] batch, BatchResponse response)
    {
        var invalidTokens = new List<string>();

        for (var index = 0; index < response.Responses.Count; index++)
        {
            var sendResponse = response.Responses[index];
            if (sendResponse.IsSuccess)
                continue;

            if (sendResponse.Exception is not FirebaseMessagingException ex)
                continue;

            if (ex.MessagingErrorCode == MessagingErrorCode.Unregistered)
            {
                invalidTokens.Add(batch[index]);
                continue;
            }

            if (
                ex.MessagingErrorCode == MessagingErrorCode.InvalidArgument
                && ex.Message.Contains("token", StringComparison.OrdinalIgnoreCase)
            )
            {
                invalidTokens.Add(batch[index]);
            }
        }

        return invalidTokens;
    }

    private static Dictionary<string, string> BuildPayloadData(
        string title,
        string body,
        Dictionary<string, string>? data
    )
    {
        var payloadData = data is null
            ? new Dictionary<string, string>(StringComparer.Ordinal)
            : new Dictionary<string, string>(data, StringComparer.Ordinal);

        payloadData.TryAdd("title", title);
        payloadData.TryAdd("body", body);

        return payloadData;
    }

    private static string? ResolveClickLink(
        IReadOnlyDictionary<string, string> data,
        string? frontendBaseUrl
    )
    {
        if (TryGetFirstNonEmptyValue(
                data,
                out var directLink,
                "actionUrl",
                "click_action",
                "url",
                "link",
                "route",
                "pathname"
            ))
        {
            return BuildWebPushLink(directLink, frontendBaseUrl);
        }

        if (TryGetFirstNonEmptyValue(data, out var accountId, "accountId"))
            return BuildWebPushLink($"/account/{accountId}", frontendBaseUrl);

        if (TryGetFirstNonEmptyValue(data, out var userId, "userId"))
            return BuildWebPushLink($"/users/{userId}", frontendBaseUrl);

        if (TryGetFirstNonEmptyValue(data, out var creditId, "creditId"))
            return BuildWebPushLink($"/credits/{creditId}", frontendBaseUrl);

        if (TryGetFirstNonEmptyValue(data, out var screen, "screen"))
        {
            return screen switch
            {
                "users" => BuildWebPushLink("/users", frontendBaseUrl),
                "transactions" or "dashboard" => BuildWebPushLink("/", frontendBaseUrl),
                _ => null,
            };
        }

        return null;
    }

    private static bool TryGetFirstNonEmptyValue(
        IReadOnlyDictionary<string, string> data,
        out string value,
        params string[] keys
    )
    {
        foreach (var key in keys)
        {
            if (data.TryGetValue(key, out var candidate) && !string.IsNullOrWhiteSpace(candidate))
            {
                value = candidate;
                return true;
            }
        }

        value = string.Empty;
        return false;
    }

    private static string BuildWebPushLink(string value, string? frontendBaseUrl)
    {
        if (Uri.TryCreate(value, UriKind.Absolute, out _))
            return value;

        var normalizedPath = value.StartsWith('/') ? value : $"/{value}";
        if (string.IsNullOrWhiteSpace(frontendBaseUrl))
            return normalizedPath;

        var baseUri = new Uri(frontendBaseUrl.TrimEnd('/') + "/", UriKind.Absolute);
        return new Uri(baseUri, normalizedPath.TrimStart('/')).ToString();
    }

    private async Task RemoveInvalidTokensAsync(
        IReadOnlyCollection<string> invalidTokens,
        CancellationToken ct
    )
    {
        var tokensToRemove = await db.FirebaseTokens.Where(token => invalidTokens.Contains(token.Token)).ToListAsync(ct);
        if (tokensToRemove.Count == 0)
            return;

        db.FirebaseTokens.RemoveRange(tokensToRemove);
        await db.SaveChangesAsync(ct);

        logger.LogInformation(
            "Removed {Count} invalid Firebase token(s) from persistence",
            tokensToRemove.Count
        );
    }
}

