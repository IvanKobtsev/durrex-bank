namespace MyApp.CreditService.Models;

public class Tariff
{
    public int Id { get; set; }
    public string Name { get; set; } = null!;
    public decimal InterestRate { get; set; }
    public int TermMonths { get; set; }
}
