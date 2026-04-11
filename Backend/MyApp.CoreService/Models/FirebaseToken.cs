using Microsoft.EntityFrameworkCore;
using MyApp.CoreService.Auth;

namespace MyApp.CoreService.Models;

[PrimaryKey(nameof(Token))]
public class FirebaseToken
{
    public int UserId { get; set; }
    public CallerRole Role { get; set; }
    public required string Token { get; set; }
}
