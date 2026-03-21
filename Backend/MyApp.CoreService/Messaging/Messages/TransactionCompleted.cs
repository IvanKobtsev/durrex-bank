using MyApp.CoreService.DTOs.Responses;

namespace MyApp.CoreService.Messaging.Messages;

public record TransactionCompleted(
    Guid MessageId,
    TransactionResponse Transaction
);
