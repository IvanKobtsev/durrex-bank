using MyApp.CoreService.Enums;

namespace MyApp.CoreService.Messaging.Messages;

public record TransactionRequested(
    Guid MessageId,
    int AccountId,
    TransactionType Type,
    decimal Amount,
    int? RelatedAccountId,
    string? Description,
    int? RequestedByUserId
);
