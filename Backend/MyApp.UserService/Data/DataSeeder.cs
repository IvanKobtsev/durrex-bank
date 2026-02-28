using Microsoft.EntityFrameworkCore;
using MyApp.UserService.Data;
using MyApp.UserService.Models;

public class DataSeeder(UserDbContext _dbContext, IConfiguration _configuration)
{
    public async Task SeedAsync(CancellationToken ct = default)
    {
        if (await _dbContext.Users.AnyAsync(ct))
            return;

        _dbContext.Users.Add(
            new AppUser
            {
                Username = "admin",
                Email = "admin@example.com",
                FirstName = "Admin",
                LastName = "User",
                TelephoneNumber = "+10000000000",
                PasswordHash = BCrypt.Net.BCrypt.HashPassword(_configuration["AdminPassword"]),
                Role = Role.Employee,
                IsBlocked = false,
            }
        );

        await _dbContext.SaveChangesAsync();
    }
}
