using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Models;

namespace MyApp.CoreService.Data.Configurations;

public class AccountConfiguration : IEntityTypeConfiguration<Account>
{
    public void Configure(EntityTypeBuilder<Account> builder)
    {
        builder.ToTable("accounts");

        builder.HasKey(a => a.Id);

        builder.Property(a => a.Id)
            .HasColumnName("id")
            .ValueGeneratedOnAdd();

        builder.Property(a => a.OwnerId)
            .HasColumnName("owner_id")
            .IsRequired();

        builder.Property(a => a.Balance)
            .HasColumnName("balance")
            .HasColumnType("decimal(18,2)")
            .HasDefaultValue(0m);

        builder.Property(a => a.Currency)
            .HasColumnName("currency")
            .HasMaxLength(3)
            .HasDefaultValue("RUB")
            .IsRequired();

        builder.Property(a => a.Status)
            .HasColumnName("status")
            .HasConversion<string>()
            .HasDefaultValue(AccountStatus.Open)
            .IsRequired();

        builder.Property(a => a.CreatedAt)
            .HasColumnName("created_at")
            .IsRequired();

        builder.Property(a => a.ClosedAt)
            .HasColumnName("closed_at");

        builder.HasIndex(a => a.OwnerId)
            .HasDatabaseName("ix_accounts_owner_id");
    }
}
