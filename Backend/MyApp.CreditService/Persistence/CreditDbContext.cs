using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using MyApp.CreditService.Models;

public class CreditDbContext(DbContextOptions<CreditDbContext> options) : DbContext(options)
{
    public DbSet<Tariff> Tariffs => Set<Tariff>();
    public DbSet<Credit> Credits => Set<Credit>();
    public DbSet<PaymentScheduleEntry> PaymentScheduleEntries => Set<PaymentScheduleEntry>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.ApplyConfiguration(new CreditConfiguration());
        modelBuilder.ApplyConfiguration(new TariffConfiguration());
        modelBuilder.ApplyConfiguration(new PaymentScheduleEntryConfiguration());
    }
}

file class TariffConfiguration : IEntityTypeConfiguration<Tariff>
{
    public void Configure(EntityTypeBuilder<Tariff> builder)
    {
        builder.HasKey(t => t.Id);
        builder.Property(t => t.Name).IsRequired().HasMaxLength(256);
        builder.Property(t => t.InterestRate).IsRequired();
        builder.Property(t => t.TermMonths).IsRequired();

        builder.HasIndex(t => t.Name).IsUnique();
    }
}

file class PaymentScheduleEntryConfiguration : IEntityTypeConfiguration<PaymentScheduleEntry>
{
    public void Configure(EntityTypeBuilder<PaymentScheduleEntry> builder)
    {
        builder.HasKey(p => p.Id);
        builder.Property(p => p.CreditId).IsRequired();
        builder.Property(p => p.DueDate).IsRequired();
        builder.Property(p => p.Amount).IsRequired();
        builder.Property(p => p.IsPaid).IsRequired();
        builder.Property(p => p.PaidAt);
    }
}

file class CreditConfiguration : IEntityTypeConfiguration<Credit>
{
    public void Configure(EntityTypeBuilder<Credit> builder)
    {
        builder.HasKey(c => c.Id);

        builder.Property(c => c.ClientId).IsRequired();

        builder.Property(c => c.AccountId).IsRequired();

        builder.Property(c => c.TariffId).IsRequired();

        builder.Property(c => c.Amount).IsRequired();

        builder.Property(c => c.RemainingBalance).IsRequired();

        builder.Property(c => c.Status).IsRequired().HasConversion<string>();

        builder.Property(c => c.IssuedAt).IsRequired();

        builder.Property(c => c.ClosedAt);

        builder
            .HasOne(c => c.Tariff)
            .WithMany()
            .HasForeignKey(c => c.TariffId)
            .OnDelete(DeleteBehavior.Restrict);

        builder
            .HasMany(c => c.Schedule)
            .WithOne(p => p.Credit)
            .HasForeignKey(p => p.CreditId)
            .OnDelete(DeleteBehavior.Restrict);
    }
}
