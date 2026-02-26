using MyApp.CreditService.Models;

namespace MyApp.CreditService.DTOs.Credits;

/// <summary>Credit summary</summary>
/// <param name="Id">Unique credit Id</param>
/// <param name="ClientId">ID of the client who took the loan</param>
/// <param name="AccountId">ID of the account the loan was credited to</param>
/// <param name="TariffName">Name of the applied tariff</param>
/// <param name="Amount">Original loan amount</param>
/// <param name="RemainingBalance">Remaining debt</param>
/// <param name="Status">Current credit status</param>
/// <param name="IssuedAt">Date and time the loan was isued</param>
public record CreditResponse(
    int Id,
    int ClientId,
    int AccountId,
    string TariffName,
    decimal Amount,
    decimal RemainingBalance,
    CreditStatus Status,
    DateTime IssuedAt
);
