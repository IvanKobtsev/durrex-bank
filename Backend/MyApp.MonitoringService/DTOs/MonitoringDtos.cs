using System.Text.Json;

namespace MyApp.MonitoringService.DTOs;

public sealed class CaptureErrorEventRequest
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
    public string? Fingerprint { get; init; }
    public DateTimeOffset? OccurredAtUtc { get; init; }
    public IReadOnlyDictionary<string, string>? Tags { get; init; }
    public JsonElement? AdditionalData { get; init; }
}

public sealed class CaptureRequestTraceRequest
{
    public string? Method { get; init; }
    public string? Path { get; init; }
    public string? QueryString { get; init; }
    public int StatusCode { get; init; }
    public double DurationMs { get; init; }
    public string? TraceId { get; init; }
    public string? UserId { get; init; }
    public string? RemoteIp { get; init; }
    public string? UserAgent { get; init; }
    public string? ExceptionType { get; init; }
    public string? ExceptionMessage { get; init; }
    public DateTimeOffset? TimestampUtc { get; init; }
}

public enum MonitoringTimeRange
{
    Last5Minutes,
    LastHour,
    Last6Hours,
    Last24Hours,
    Last7Days,
}

public sealed record MonitoringChartPoint(
    DateTimeOffset BucketStartUtc,
    string Label,
    double Value
);

public sealed record MonitoringChartSeries(
    string Name,
    string Color,
    IReadOnlyList<MonitoringChartPoint> Points
);

public sealed record MonitoringTrendsSnapshot(
    MonitoringTimeRange TimeRange,
    string TimeRangeLabel,
    DateTimeOffset FromUtc,
    DateTimeOffset ToUtc,
    IReadOnlyList<MonitoringChartSeries> RequestSeries,
    IReadOnlyList<MonitoringChartSeries> EventSeries
);

public sealed record MonitoringDashboardSnapshot(
    int TotalEvents,
    int EventsLast24Hours,
    int AffectedServices,
    DateTimeOffset? LatestReceivedAtUtc,
    IReadOnlyList<MonitoringEventListItem> Events
);

public sealed record MonitoringEventListItem(
    string EventId,
    string Service,
    string Environment,
    string Level,
    string Message,
    string? ExceptionType,
    string? StackTrace,
    string? RequestMethod,
    string? RequestPath,
    string? TraceId,
    string? UserId,
    string Fingerprint,
    DateTimeOffset OccurredAtUtc,
    DateTimeOffset ReceivedAtUtc,
    IReadOnlyDictionary<string, string> Tags,
    string? AdditionalDataJson
)
{
    public string LevelLabel =>
        string.IsNullOrWhiteSpace(Level) ? "ERROR" : Level.ToUpperInvariant();
}

public sealed record MonitoringRequestSnapshot(
    int TotalRequests,
    int RequestsLast24Hours,
    int ErrorResponsesLast24Hours,
    double AvgDurationMsLast24Hours,
    double ErrorRateLast5MinPct,
    DateTimeOffset? LatestRequestAtUtc,
    IReadOnlyList<MonitoringRequestListItem> Requests
);

public sealed record MonitoringRequestListItem(
    string TraceEntryId,
    string Method,
    string Path,
    string? QueryString,
    int StatusCode,
    double DurationMs,
    bool IsSuccess,
    string? TraceId,
    string? UserId,
    string? RemoteIp,
    string? UserAgent,
    string? ExceptionType,
    string? ExceptionMessage,
    DateTimeOffset TimestampUtc
)
{
    public string StatusLabel =>
        IsSuccess ? "Success"
        : StatusCode / 100 == 4 ? "Client Error"
        : "Server Error";
}
