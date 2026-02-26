namespace MyApp.CreditService.Models;

public class PaymentScheduleEntry
{
    public int Id { get; set; }
    public int CreditId { get; set; }
    public Credit Credit { get; set; } = null!;
    public DateTime DueDate { get; set; }
    public decimal Amount { get; set; }
    public bool IsPaid { get; set; }
    public DateTime? PaidAt { get; set; }
}
