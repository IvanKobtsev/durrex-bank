using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using MyApp.UserService.Models;

namespace MyApp.UserService.Data;

public class UserDbContext(DbContextOptions<UserDbContext> options) : DbContext(options)
{
    public DbSet<AppUser> Users => Set<AppUser>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.ApplyConfiguration(new AppUserConfiguration());
    }
}

file class AppUserConfiguration : IEntityTypeConfiguration<AppUser>
{
    public void Configure(EntityTypeBuilder<AppUser> builder)
    {
        builder.HasKey(u => u.Id);

        builder.Property(u => u.Email).IsRequired().HasMaxLength(64);

        builder.HasIndex(u => u.Email).IsUnique();

        builder.Property(u => u.Username).IsRequired().HasMaxLength(64);

        builder.HasIndex(u => u.Username).IsUnique();

        builder.Property(u => u.FirstName).IsRequired().HasMaxLength(128);
        builder.Property(u => u.LastName).IsRequired().HasMaxLength(128);

        builder.Property(u => u.TelephoneNumber).IsRequired().HasMaxLength(32);
        builder.HasIndex(u => u.TelephoneNumber).IsUnique();

        builder.Property(u => u.PasswordHash).IsRequired();

        builder.Property(u => u.Role).IsRequired();
    }
}
