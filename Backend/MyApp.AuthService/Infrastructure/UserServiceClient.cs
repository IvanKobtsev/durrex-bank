namespace MyApp.AuthService.Infrastructure;

public class UserAuthProfile
{
    public int UserId { get; set; }
    public List<string> ExpandedRoles { get; set; } = [];
    public bool IsBlocked { get; set; }
}

public class UserServiceClient(HttpClient http, ILogger<UserServiceClient> logger)
{
    /// <summary>
    /// Returns the user's expanded roles and block status.
    /// Throws if UserService is unreachable or returns an error — a failure here means
    /// the token would be issued with no roles, which is a security issue.
    /// </summary>
    public async Task<UserAuthProfile> GetAuthProfileAsync(int userId, CancellationToken ct = default)
    {
        var url = $"/internal/users/{userId}/auth-profile";
        HttpResponseMessage response;

        try
        {
            response = await http.GetAsync(url, ct);
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "UserService unreachable when fetching auth profile for user {UserId}", userId);
            throw;
        }

        if (!response.IsSuccessStatusCode)
        {
            var body = await response.Content.ReadAsStringAsync(ct);
            logger.LogError(
                "UserService returned {StatusCode} for GET {Url}. Body: {Body}",
                (int)response.StatusCode, url, body);
            throw new HttpRequestException(
                $"UserService returned {(int)response.StatusCode} for user {userId} auth profile.");
        }

        return await response.Content.ReadFromJsonAsync<UserAuthProfile>(cancellationToken: ct)
            ?? throw new InvalidOperationException($"UserService returned null body for user {userId} auth profile.");
    }
}
