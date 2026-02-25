using MyApp.CoreService.Enums;

namespace MyApp.CoreService.DTOs.Responses;

public record AccountResponse(
    int Id,
    int OwnerId,
    decimal Balance,
    string Currency,
    AccountStatus Status,
    DateTimeOffset CreatedAt,
    DateTimeOffset? ClosedAt
);
