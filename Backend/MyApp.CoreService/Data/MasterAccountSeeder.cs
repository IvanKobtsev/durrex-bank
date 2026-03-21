using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Models;

namespace MyApp.CoreService.Data;

public static class MasterAccountSeeder
{
    public static async Task SeedAsync(CoreDbContext db, IConfiguration config)
    {
        var masterId = config.GetValue<int>("Bank:MasterAccountId");
        if (!await db.Accounts.AnyAsync(a => a.Id == masterId))
        {
            db.Accounts.Add(new Account
            {
                Id = masterId,
                OwnerId = 0,
                Balance = 100_000_000m,
                Currency = "RUB",
                Status = AccountStatus.Open,
                CreatedAt = DateTimeOffset.UtcNow
            });
            await db.SaveChangesAsync();
        }
    }
}
