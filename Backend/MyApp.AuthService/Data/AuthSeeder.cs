using Microsoft.AspNetCore.Identity;
using MyApp.AuthService.Models;

namespace MyApp.AuthService.Data;

public class AuthSeeder(
    UserManager<ApplicationUser> userManager,
    IConfiguration configuration,
    ILogger<AuthSeeder> logger
)
{
    public async Task SeedAsync(CancellationToken ct = default)
    {
        const string adminEmail = "admin@example.com";

        if (await userManager.FindByEmailAsync(adminEmail) is not null)
        {
            logger.LogInformation("Admin user already exists, skipping seed");
            return;
        }

        var admin = new ApplicationUser
        {
            UserName = adminEmail,
            Email = adminEmail,
            EmailConfirmed = true,
        };

        var password =
            configuration["AdminPassword"]
            ?? throw new InvalidOperationException("AdminPassword not configured");

        var result = await userManager.CreateAsync(admin, password);

        if (result.Succeeded)
        {
            logger.LogInformation("Default admin user seeded successfully");
        }
        else
        {
            var errors = string.Join("; ", result.Errors.Select(e => e.Description));
            logger.LogError("Failed to seed admin user: {Errors}", errors);
        }
    }
}
