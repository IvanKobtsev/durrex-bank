using MyApp.CreditService.Models;

namespace MyApp.CreditService.DTOs.Credits;

/// <summary>Full credit details including repayment schedule</summary>
/// <param name="Id">Unique credit ID</param>
/// <param name="ClientId">ID of the client who took the loan</param>
/// <param name="AccountId">ID of the account the loan was credited to</param>
/// <param name="TariffName">Name of the applied tariff</param>
/// <param name="Amount">Original loan amount</param>
/// <param name="RemainingBalance">Remainig debt</param>
/// <param name="Status">Current credit status (Active / Closed)</param>
/// <param name="IssuedAt">Date and time the loan was issued</param>
/// <param name="NextPaymentDate">Due date of the next pending payment, if any</param>
/// <param name="Schedule">Full repayment schedule</param>
public record CreditDetailResponse(
    int Id,
    int ClientId,
    int AccountId,
    string TariffName,
    decimal Amount,
    decimal RemainingBalance,
    CreditStatus Status,
    DateTime IssuedAt,
    DateTime? NextPaymentDate,
    List<PaymentScheduleEntryResponse> Schedule
);
