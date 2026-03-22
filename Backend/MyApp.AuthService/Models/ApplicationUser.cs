using Microsoft.AspNetCore.Identity;

namespace MyApp.AuthService.Models;

// Minimal Identity user — only auth concerns.
// Profile data (role, name, phone) lives in UserService.
public class ApplicationUser : IdentityUser<int>
{
    // Id, UserName, Email, PasswordHash, SecurityStamp etc. come from IdentityUser<int>
}
