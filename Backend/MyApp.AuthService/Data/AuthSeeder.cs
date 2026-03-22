using Microsoft.AspNetCore.Identity;
using MyApp.AuthService.Models;

namespace MyApp.AuthService.Data;

public static class AuthSeeder
{
    public static async Task SeedAsync(
        UserManager<ApplicationUser> userManager,
        IConfiguration config)
    {
        const string adminEmail = "admin@durrex.local";
        if (await userManager.FindByEmailAsync(adminEmail) is not null)
            return;

        var admin = new ApplicationUser
        {
            Id = 1,   // Must match the ID seeded by UserService's DataSeeder
            UserName = adminEmail,
            Email = adminEmail,
            EmailConfirmed = true
        };

        // Password from config (set via environment variable in production)
        await userManager.CreateAsync(admin, config["AdminPassword"]
            ?? throw new InvalidOperationException("AdminPassword not configured"));
    }
}
