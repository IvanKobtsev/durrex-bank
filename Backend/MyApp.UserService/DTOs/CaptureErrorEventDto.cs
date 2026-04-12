namespace MyApp.UserService.DTOs;

public sealed class CaptureErrorEventDto
{
    public string? Service { get; init; }
    public string? Environment { get; init; }
    public string? Level { get; init; }
    public string? Message { get; init; }
    public string? ExceptionType { get; init; }
    public string? StackTrace { get; init; }
    public string? RequestMethod { get; init; }
    public string? RequestPath { get; init; }
    public string? TraceId { get; init; }
    public string? UserId { get; init; }
    public DateTimeOffset? OccurredAtUtc { get; init; }
}
