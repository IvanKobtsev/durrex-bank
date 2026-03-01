namespace MyApp.UserService.Auth;

public enum CallerRole { Internal, Employee, Client }

public interface ICurrentUserContext
{
    int? UserId { get; }
    CallerRole Role { get; }
    bool IsEmployee => Role == CallerRole.Employee;
    bool IsClient   => Role == CallerRole.Client;
}

public sealed class CurrentUserContext : ICurrentUserContext
{
    public int? UserId { get; init; }
    public CallerRole Role { get; init; }
}
