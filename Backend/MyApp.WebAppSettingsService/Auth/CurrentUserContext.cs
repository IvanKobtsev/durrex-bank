namespace MyApp.WebAppSettingsService.Auth;

public interface ICurrentUserContext
{
    int? UserId { get; }
    IReadOnlySet<string> Roles { get; }
    bool IsClient => Roles.Contains("Client") && !Roles.Contains("Employee");
    bool IsEmployee => Roles.Contains("Employee");
    bool IsInternal => Roles.Count == 0;
}

public sealed class CurrentUserContext : ICurrentUserContext
{
    public int? UserId { get; init; }
    public IReadOnlySet<string> Roles { get; init; } =
        new HashSet<string>(StringComparer.OrdinalIgnoreCase);
}
