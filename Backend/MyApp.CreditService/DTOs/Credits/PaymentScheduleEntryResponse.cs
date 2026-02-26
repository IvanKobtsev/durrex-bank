namespace MyApp.CreditService.DTOs.Credits;

/// <summary>A single entry in a credit's repayment schedule</summary>
/// <param name="Id">Entry Id</param>
/// <param name="DueDate">Date when the payment is due</param>
/// <param name="Amount">Payment amount</param>
/// <param name="IsPaid">Whether the payment has been made</param>
/// <param name="PaidAt">Timestamp of payment (if paid)</param>
public record PaymentScheduleEntryResponse(
    int Id,
    DateTime DueDate,
    decimal Amount,
    bool IsPaid,
    DateTime? PaidAt
);
