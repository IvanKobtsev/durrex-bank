namespace MyApp.UserService.Infrastructure;

public class AuthServiceClient(HttpClient http, ILogger<AuthServiceClient> logger)
{
    /// <summary>
    /// Requests an invite link from AuthService for a newly created user.
    /// Returns the invite URL, or null if AuthService is unavailable.
    /// Failures are logged at Error level — the caller should surface the missing URL to the employee.
    /// </summary>
    public async Task<string?> CreateInviteAsync(int userId, string email, CancellationToken ct = default)
    {
        const string url = "/internal/invite";
        HttpResponseMessage response;

        try
        {
            response = await http.PostAsJsonAsync(url, new { UserId = userId, Email = email }, ct);
        }
        catch (Exception ex)
        {
            logger.LogError(ex,
                "AuthService unreachable when creating invite for user {UserId} ({Email})",
                userId, email);
            return null;
        }

        if (!response.IsSuccessStatusCode)
        {
            var body = await response.Content.ReadAsStringAsync(ct);
            logger.LogError(
                "AuthService returned {StatusCode} for POST {Url} (user {UserId}). Body: {Body}",
                (int)response.StatusCode, url, userId, body);
            return null;
        }

        var result = await response.Content.ReadFromJsonAsync<InviteResponse>(cancellationToken: ct);
        if (result?.InviteUrl is null)
        {
            logger.LogError(
                "AuthService returned a success response for user {UserId} but InviteUrl was missing",
                userId);
        }

        return result?.InviteUrl;
    }

    private record InviteResponse(string InviteUrl);
}
