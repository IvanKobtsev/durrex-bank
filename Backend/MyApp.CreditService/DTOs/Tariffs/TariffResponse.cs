namespace MyApp.CreditService.DTOs.Tariffs;

/// <summary>Credit tariff details</summary>
/// <param name="Id">Unique tariff ID</param>
/// <param name="Name">Tariff display name</param>
/// <param name="InterestRate">Annual interest rate (e.g. 0.12 for 12%)</param>
/// <param name="TermMonths">Fixed loan duration in months</param>
public record TariffResponse(int Id, string Name, decimal InterestRate, int TermMonths);
