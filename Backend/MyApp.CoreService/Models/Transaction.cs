using MyApp.CoreService.Enums;

namespace MyApp.CoreService.Models;

public class Transaction
{
    public long Id { get; set; }
    public int AccountId { get; set; }
    public TransactionType Type { get; set; }
    public decimal Amount { get; set; }
    public decimal BalanceBefore { get; set; }
    public decimal BalanceAfter { get; set; }
    public int? RelatedAccountId { get; set; }
    public string? Description { get; set; }
    public DateTimeOffset CreatedAt { get; set; }

    public Account Account { get; set; } = null!;
}
