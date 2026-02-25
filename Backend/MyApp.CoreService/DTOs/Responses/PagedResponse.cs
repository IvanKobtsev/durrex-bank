namespace MyApp.CoreService.DTOs.Responses;

public record PagedResponse<T>(
    IReadOnlyList<T> Items,
    int Page,
    int PageSize,
    int TotalCount,
    int TotalPages
);
