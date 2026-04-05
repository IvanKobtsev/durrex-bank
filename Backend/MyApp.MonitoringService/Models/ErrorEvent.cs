namespace MyApp.MonitoringService.Models;

public sealed class ErrorEvent
{
    public long Id { get; set; }
    public string EventId { get; set; } = Guid.NewGuid().ToString("n");
    public string Service { get; set; } = "unknown-service";
    public string Environment { get; set; } = "Production";
    public string Level { get; set; } = "error";
    public string Message { get; set; } = "Unhandled error";
    public string? ExceptionType { get; set; }
    public string? StackTrace { get; set; }
    public string? RequestMethod { get; set; }
    public string? RequestPath { get; set; }
    public string? TraceId { get; set; }
    public string? UserId { get; set; }
    public string Fingerprint { get; set; } = string.Empty;
    public string? TagsJson { get; set; }
    public string? AdditionalDataJson { get; set; }
    public DateTimeOffset OccurredAtUtc { get; set; }
    public DateTimeOffset ReceivedAtUtc { get; set; }
}
