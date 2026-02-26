using System.ComponentModel.DataAnnotations;

namespace MyApp.CreditService.DTOs.Credits;

/// <summary>Request to issue a new loan</summary>
/// <param name="ClientId">ID of the client receiving the loan</param>
/// <param name="AccountId">ID of the account to credit the loan amount to</param>
/// <param name="TariffId">ID of the tariff to apply</param>
/// <param name="Amount">Loan amount</param>
public record IssueCreditRequest(
    [Required] int ClientId,
    [Required] int AccountId,
    [Required] int TariffId,
    [Required] [Range(0.01, double.MaxValue)] decimal Amount
);
