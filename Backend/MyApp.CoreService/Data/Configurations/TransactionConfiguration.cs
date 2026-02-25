using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using MyApp.CoreService.Models;

namespace MyApp.CoreService.Data.Configurations;

public class TransactionConfiguration : IEntityTypeConfiguration<Transaction>
{
    public void Configure(EntityTypeBuilder<Transaction> builder)
    {
        builder.ToTable("transactions");

        builder.HasKey(t => t.Id);

        builder.Property(t => t.Id)
            .HasColumnName("id")
            .ValueGeneratedOnAdd();

        builder.Property(t => t.AccountId)
            .HasColumnName("account_id")
            .IsRequired();

        builder.Property(t => t.Type)
            .HasColumnName("type")
            .HasConversion<string>()
            .IsRequired();

        builder.Property(t => t.Amount)
            .HasColumnName("amount")
            .HasColumnType("decimal(18,2)")
            .IsRequired();

        builder.Property(t => t.BalanceBefore)
            .HasColumnName("balance_before")
            .HasColumnType("decimal(18,2)")
            .IsRequired();

        builder.Property(t => t.BalanceAfter)
            .HasColumnName("balance_after")
            .HasColumnType("decimal(18,2)")
            .IsRequired();

        builder.Property(t => t.RelatedAccountId)
            .HasColumnName("related_account_id");

        builder.Property(t => t.Description)
            .HasColumnName("description")
            .HasMaxLength(500);

        builder.Property(t => t.CreatedAt)
            .HasColumnName("created_at")
            .IsRequired();

        builder.HasOne(t => t.Account)
            .WithMany(a => a.Transactions)
            .HasForeignKey(t => t.AccountId)
            .OnDelete(DeleteBehavior.Restrict);

        builder.HasIndex(t => t.AccountId)
            .HasDatabaseName("ix_transactions_account_id");

        builder.HasIndex(t => new { t.AccountId, t.CreatedAt })
            .HasDatabaseName("ix_transactions_account_id_created_at");
    }
}
