namespace MyApp.UserService.Models;

public class AppUser
{
    public int Id { get; set; }
    public string Username { get; set; } = null!;
    public string PasswordHash { get; set; } = null!;
    public Role Role { get; set; }
    public bool IsBlocked { get; set; }
}
