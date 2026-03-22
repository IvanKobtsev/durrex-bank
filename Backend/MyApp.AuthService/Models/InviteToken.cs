namespace MyApp.AuthService.Models;

public class InviteToken
{
    public string Token { get; set; } = null!;        // PK — random 32-byte hex string
    public int UserId { get; set; }
    public DateTimeOffset ExpiresAt { get; set; }
    public bool IsUsed { get; set; }
}
