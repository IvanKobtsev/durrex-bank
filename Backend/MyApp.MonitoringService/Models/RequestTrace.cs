namespace MyApp.MonitoringService.Models;

public sealed class RequestTrace
{
    public long Id { get; set; }
    public string TraceEntryId { get; set; } = Guid.NewGuid().ToString("n");
    public string Method { get; set; } = "GET";
    public string Path { get; set; } = "/";
    public string? QueryString { get; set; }
    public int StatusCode { get; set; }
    public double DurationMs { get; set; }
    public bool IsSuccess { get; set; }
    public string? TraceId { get; set; }
    public string? UserId { get; set; }
    public string? RemoteIp { get; set; }
    public string? UserAgent { get; set; }
    public string? ExceptionType { get; set; }
    public string? ExceptionMessage { get; set; }
    public DateTimeOffset TimestampUtc { get; set; }
}
