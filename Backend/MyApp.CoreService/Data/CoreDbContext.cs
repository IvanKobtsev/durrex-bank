using Microsoft.EntityFrameworkCore;
using MyApp.CoreService.Data.Configurations;
using MyApp.CoreService.Models;

namespace MyApp.CoreService.Data;

public class CoreDbContext : DbContext
{
    public CoreDbContext(DbContextOptions<CoreDbContext> options) : base(options) { }

    public DbSet<Account> Accounts => Set<Account>();
    public DbSet<Transaction> Transactions => Set<Transaction>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.ApplyConfiguration(new AccountConfiguration());
        modelBuilder.ApplyConfiguration(new TransactionConfiguration());
        base.OnModelCreating(modelBuilder);
    }
}
