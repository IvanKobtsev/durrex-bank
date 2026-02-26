using MyApp.CoreService.Enums;

namespace MyApp.CoreService.DTOs.Responses;

public record TransactionResponse(
    long Id,
    int AccountId,
    TransactionType Type,
    decimal Amount,
    decimal BalanceBefore,
    decimal BalanceAfter,
    int? RelatedAccountId,
    string? Description,
    DateTimeOffset CreatedAt
);
