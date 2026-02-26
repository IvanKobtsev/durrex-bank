using MyApp.CoreService.Enums;

namespace MyApp.CoreService.Models;

public class Account
{
    public int Id { get; set; }
    public int OwnerId { get; set; }
    public decimal Balance { get; set; } = 0m;
    public string Currency { get; set; } = "RUB";
    public AccountStatus Status { get; set; } = AccountStatus.Open;
    public DateTimeOffset CreatedAt { get; set; }
    public DateTimeOffset? ClosedAt { get; set; }

    public ICollection<Transaction> Transactions { get; set; } = new List<Transaction>();
}
