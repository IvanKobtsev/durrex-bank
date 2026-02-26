namespace MyApp.CoreService.Auth;

public enum CallerRole { Internal, Employee, Client }

public interface ICurrentUserContext
{
    int? UserId { get; }
    CallerRole Role { get; }
    bool IsClient   => Role == CallerRole.Client;
    bool IsInternal => Role == CallerRole.Internal;
}

public sealed class CurrentUserContext : ICurrentUserContext
{
    public int? UserId { get; init; }
    public CallerRole Role { get; init; }
}
