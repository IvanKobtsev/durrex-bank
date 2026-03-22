using Microsoft.EntityFrameworkCore;
using MyApp.WebAppSettingsService.Models;

namespace MyApp.WebAppSettingsService.Data;

public class SettingsDbContext(DbContextOptions<SettingsDbContext> options) : DbContext(options)
{
    public DbSet<UserSettings> Settings => Set<UserSettings>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<UserSettings>(b =>
        {
            b.ToTable("user_settings");
            b.HasKey(s => s.UserId);
            b.Property(s => s.UserId).HasColumnName("user_id").ValueGeneratedNever();
            b.Property(s => s.Theme).HasColumnName("theme").HasMaxLength(16).IsRequired();
            b.Property(s => s.UpdatedAt).HasColumnName("updated_at");
        });
    }
}
