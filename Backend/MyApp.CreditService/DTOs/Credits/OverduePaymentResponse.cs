namespace MyApp.CreditService.DTOs.Credits;

public record OverduePaymentResponse(
    int EntryId,
    int CreditId,
    DateTime DueDate,
    decimal Amount,
    int DaysOverdue
);
