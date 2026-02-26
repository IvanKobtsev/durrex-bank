namespace MyApp.CreditService.Models;

public class Credit
{
    public int Id { get; set; }
    public int ClientId { get; set; }
    public int AccountId { get; set; }
    public int TariffId { get; set; }
    public Tariff Tariff { get; set; } = null!;
    public decimal Amount { get; set; }
    public decimal RemainingBalance { get; set; }
    public CreditStatus Status { get; set; }
    public DateTime IssuedAt { get; set; }
    public DateTime? ClosedAt { get; set; }

    public ICollection<PaymentScheduleEntry> Schedule { get; set; } = [];
}
