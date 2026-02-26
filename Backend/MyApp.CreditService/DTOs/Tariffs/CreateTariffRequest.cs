using System.ComponentModel.DataAnnotations;

namespace MyApp.CreditService.DTOs.Tariffs;

/// <summary>Request to create a new credit tariff</summary>
/// <param name="Name">Tariff display name</param>
/// <param name="InterestRate">Annual interest rate (e.g. 0.12 for 12%)</param>
public record CreateTariffRequest(
    [Required] [MaxLength(128)] string Name,
    [Required] [Range(0.001, 10.0)] decimal InterestRate
);
