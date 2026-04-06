using System.Text.Json;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using MyApp.MonitoringService.Data;
using MyApp.MonitoringService.DTOs;
using MyApp.MonitoringService.Hubs;
using MyApp.MonitoringService.Models;

namespace MyApp.MonitoringService.Services;

public sealed class MonitoringEventService(
    IDbContextFactory<MonitoringDbContext> dbContextFactory,
    IConfiguration configuration,
    ILogger<MonitoringEventService> logger,
    IHubContext<MonitoringHub> hubContext
)
{
    private static readonly JsonSerializerOptions SerializerOptions = new(
        JsonSerializerDefaults.Web
    )
    {
        WriteIndented = true,
    };

    private static readonly object PruneLock = new();
    private static DateTimeOffset _lastPruneAtUtc = DateTimeOffset.MinValue;

    private readonly int _retentionDays = Math.Max(
        0,
        configuration.GetValue<int?>("Monitoring:RetentionDays") ?? 30
    );

    private readonly int _defaultEventsLimit = Math.Max(
        1,
        configuration.GetValue<int?>("Monitoring:MaxEventsOnDashboard") ?? 250
    );

    private readonly int _defaultRequestsLimit = Math.Max(
        1,
        configuration.GetValue<int?>("Monitoring:MaxRequestsOnDashboard") ?? 250
    );

    private readonly bool _captureSuccessResponses =
        configuration.GetValue<bool?>("Monitoring:RequestTracing:CaptureSuccessResponses") ?? true;

    public async Task<MonitoringEventListItem> CaptureAsync(
        CaptureErrorEventRequest request,
        CancellationToken cancellationToken = default
    )
    {
        await using var db = await dbContextFactory.CreateDbContextAsync(cancellationToken);
        await MaybePruneExpiredDataAsync(db, cancellationToken);

        var occurredAt = request.OccurredAtUtc?.ToUniversalTime() ?? DateTimeOffset.UtcNow;
        var receivedAt = DateTimeOffset.UtcNow;
        var message = NormalizeRequired(
            request.Message,
            NormalizeOptional(request.ExceptionType, 500) ?? "Unhandled error",
            4000
        );
        var service = NormalizeRequired(request.Service, "unknown-service", 120);
        var environment = NormalizeRequired(request.Environment, "Production", 80);
        var level = NormalizeRequired(request.Level, "error", 32).ToLowerInvariant();
        var normalizedStackTrace = NormalizeOptional(request.StackTrace);
        var fingerprint = NormalizeRequired(
            request.Fingerprint,
            $"{service}:{NormalizeOptional(request.ExceptionType, 120) ?? "error"}:{message}".ToLowerInvariant(),
            300
        );

        var record = new ErrorEvent
        {
            EventId = Guid.NewGuid().ToString("n"),
            Service = service,
            Environment = environment,
            Level = level,
            Message = message,
            ExceptionType = NormalizeOptional(request.ExceptionType, 500),
            StackTrace = NormalizeOptional(normalizedStackTrace),
            RequestMethod = NormalizeOptional(request.RequestMethod, 16)?.ToUpperInvariant(),
            RequestPath = NormalizeOptional(request.RequestPath, 2048),
            TraceId = NormalizeOptional(request.TraceId, 128),
            UserId = NormalizeOptional(request.UserId, 128),
            Fingerprint = fingerprint,
            TagsJson = SerializeTags(request.Tags),
            AdditionalDataJson = SerializeJson(request.AdditionalData),
            OccurredAtUtc = occurredAt,
            ReceivedAtUtc = receivedAt,
        };

        db.ErrorEvents.Add(record);
        await db.SaveChangesAsync(cancellationToken);

        logger.LogInformation(
            "Captured monitoring event {EventId} from {Service} ({Level})",
            record.EventId,
            record.Service,
            record.Level
        );

        await hubContext.Clients.All.SendAsync(MonitoringHub.EventCaptured, cancellationToken);

        return Map(record);
    }

    public async Task CaptureRequestTraceAsync(
        CaptureRequestTraceRequest request,
        CancellationToken cancellationToken = default
    )
    {
        var statusCode = request.StatusCode <= 0 ? 200 : request.StatusCode;
        var isSuccess = statusCode < 400 && string.IsNullOrWhiteSpace(request.ExceptionType);

        if (!_captureSuccessResponses && isSuccess)
        {
            return;
        }

        await using var db = await dbContextFactory.CreateDbContextAsync(cancellationToken);
        await MaybePruneExpiredDataAsync(db, cancellationToken);

        var trace = new RequestTrace
        {
            TraceEntryId = Guid.NewGuid().ToString("n"),
            Method = NormalizeRequired(request.Method, "GET", 16).ToUpperInvariant(),
            Path = NormalizeRequired(request.Path, "/", 2048),
            QueryString = NormalizeOptional(request.QueryString, 4096),
            StatusCode = statusCode,
            DurationMs = Math.Clamp(request.DurationMs, 0, 120000),
            IsSuccess = isSuccess,
            TraceId = NormalizeOptional(request.TraceId, 128),
            UserId = NormalizeOptional(request.UserId, 128),
            RemoteIp = NormalizeOptional(request.RemoteIp, 64),
            UserAgent = NormalizeOptional(request.UserAgent, 500),
            ExceptionType = NormalizeOptional(request.ExceptionType, 500),
            ExceptionMessage = NormalizeOptional(request.ExceptionMessage, 2000),
            TimestampUtc = request.TimestampUtc?.ToUniversalTime() ?? DateTimeOffset.UtcNow,
        };

        db.RequestTraces.Add(trace);
        await db.SaveChangesAsync(cancellationToken);

        await hubContext.Clients.All.SendAsync(
            MonitoringHub.RequestTraceCaptured,
            cancellationToken
        );
    }

    public async Task<IReadOnlyList<MonitoringEventListItem>> GetRecentEventsAsync(
        int? maxEvents = null,
        CancellationToken cancellationToken = default
    )
    {
        var limit = NormalizeLimit(maxEvents, _defaultEventsLimit);

        await using var db = await dbContextFactory.CreateDbContextAsync(cancellationToken);
        var events = await db
            .ErrorEvents.AsNoTracking()
            .OrderByDescending(x => x.ReceivedAtUtc)
            .Take(limit)
            .ToListAsync(cancellationToken);

        return events.Select(Map).ToList();
    }

    public async Task<MonitoringDashboardSnapshot> GetDashboardSnapshotAsync(
        int? maxEvents = null,
        CancellationToken cancellationToken = default
    )
    {
        var limit = NormalizeLimit(maxEvents, _defaultEventsLimit);
        var last24HoursThreshold = DateTimeOffset.UtcNow.AddHours(-24);

        await using var db = await dbContextFactory.CreateDbContextAsync(cancellationToken);

        var totalEvents = await db.ErrorEvents.CountAsync(cancellationToken);
        var eventsLast24Hours = await db.ErrorEvents.CountAsync(
            x => x.ReceivedAtUtc >= last24HoursThreshold,
            cancellationToken
        );
        var affectedServices = await db
            .ErrorEvents.Select(x => x.Service)
            .Distinct()
            .CountAsync(cancellationToken);
        var latestReceivedAtUtc = await db
            .ErrorEvents.AsNoTracking()
            .OrderByDescending(x => x.ReceivedAtUtc)
            .Select(x => (DateTimeOffset?)x.ReceivedAtUtc)
            .FirstOrDefaultAsync(cancellationToken);
        var events = await db
            .ErrorEvents.AsNoTracking()
            .OrderByDescending(x => x.ReceivedAtUtc)
            .Take(limit)
            .ToListAsync(cancellationToken);

        return new MonitoringDashboardSnapshot(
            totalEvents,
            eventsLast24Hours,
            affectedServices,
            latestReceivedAtUtc,
            events.Select(Map).ToList()
        );
    }

    public async Task<IReadOnlyList<MonitoringRequestListItem>> GetRecentRequestTracesAsync(
        int? maxRequests = null,
        CancellationToken cancellationToken = default
    )
    {
        var limit = NormalizeLimit(maxRequests, _defaultRequestsLimit);

        await using var db = await dbContextFactory.CreateDbContextAsync(cancellationToken);
        var traces = await db
            .RequestTraces.AsNoTracking()
            .OrderByDescending(x => x.TimestampUtc)
            .Take(limit)
            .ToListAsync(cancellationToken);

        return traces.Select(Map).ToList();
    }

    public async Task<MonitoringRequestSnapshot> GetRequestSnapshotAsync(
        int? maxRequests = null,
        CancellationToken cancellationToken = default
    )
    {
        var limit = NormalizeLimit(maxRequests, _defaultRequestsLimit);
        var last24HoursThreshold = DateTimeOffset.UtcNow.AddHours(-24);
        var last5MinThreshold = DateTimeOffset.UtcNow.AddMinutes(-5);

        await using var db = await dbContextFactory.CreateDbContextAsync(cancellationToken);

        var totalRequests = await db.RequestTraces.CountAsync(cancellationToken);
        var requestsLast24Hours = await db.RequestTraces.CountAsync(
            x => x.TimestampUtc >= last24HoursThreshold,
            cancellationToken
        );
        var errorResponsesLast24Hours = await db.RequestTraces.CountAsync(
            x => x.TimestampUtc >= last24HoursThreshold && !x.IsSuccess && x.StatusCode / 100 == 5,
            cancellationToken
        );
        var avgDurationMsLast24Hours =
            await db
                .RequestTraces.Where(x => x.TimestampUtc >= last24HoursThreshold)
                .Select(x => (double?)x.DurationMs)
                .AverageAsync(cancellationToken) ?? 0;
        var totalLast5Min = await db.RequestTraces.CountAsync(
            x => x.TimestampUtc >= last5MinThreshold,
            cancellationToken
        );
        var errorsLast5Min = await db.RequestTraces.CountAsync(
            x => x.TimestampUtc >= last5MinThreshold && !x.IsSuccess,
            cancellationToken
        );
        var errorRateLast5MinPct =
            totalLast5Min > 0 ? Math.Round((double)errorsLast5Min / totalLast5Min * 100, 1) : 0.0;
        var latestRequestAtUtc = await db
            .RequestTraces.AsNoTracking()
            .OrderByDescending(x => x.TimestampUtc)
            .Select(x => (DateTimeOffset?)x.TimestampUtc)
            .FirstOrDefaultAsync(cancellationToken);
        var requests = await db
            .RequestTraces.AsNoTracking()
            .OrderByDescending(x => x.TimestampUtc)
            .Take(limit)
            .ToListAsync(cancellationToken);

        return new MonitoringRequestSnapshot(
            totalRequests,
            requestsLast24Hours,
            errorResponsesLast24Hours,
            Math.Round(avgDurationMsLast24Hours, 2),
            errorRateLast5MinPct,
            latestRequestAtUtc,
            requests.Select(Map).ToList()
        );
    }

    public async Task<MonitoringTrendsSnapshot> GetTrendsSnapshotAsync(
        MonitoringTimeRange timeRange,
        CancellationToken cancellationToken = default
    )
    {
        var range = GetTrendRange(timeRange);
        var toUtcExclusive = AlignDown(DateTimeOffset.UtcNow, range.BucketSize)
            .Add(range.BucketSize);
        var fromUtc = toUtcExclusive - range.Duration;
        var bucketStarts = BuildBucketStarts(fromUtc, toUtcExclusive, range.BucketSize);

        await using var db = await dbContextFactory.CreateDbContextAsync(cancellationToken);

        var requestRows = await db
            .RequestTraces.AsNoTracking()
            .Where(x => x.TimestampUtc >= fromUtc && x.TimestampUtc < toUtcExclusive)
            .Select(x => new RequestTrendRow(x.TimestampUtc, x.StatusCode))
            .ToListAsync(cancellationToken);

        var eventRows = await db
            .ErrorEvents.AsNoTracking()
            .Where(x => x.ReceivedAtUtc >= fromUtc && x.ReceivedAtUtc < toUtcExclusive)
            .Select(x => new EventTrendRow(x.ReceivedAtUtc, x.Level))
            .ToListAsync(cancellationToken);

        var totalRequests = bucketStarts.ToDictionary(bucket => bucket, _ => 0d);
        var clientErrors = bucketStarts.ToDictionary(bucket => bucket, _ => 0d);
        var serverErrors = bucketStarts.ToDictionary(bucket => bucket, _ => 0d);

        foreach (var row in requestRows)
        {
            var bucket = AlignDown(row.TimestampUtc, range.BucketSize);

            if (!totalRequests.ContainsKey(bucket))
            {
                continue;
            }

            totalRequests[bucket] += 1;

            if (row.StatusCode / 100 == 4)
            {
                clientErrors[bucket] += 1;
            }
            else if (row.StatusCode / 100 == 5)
            {
                serverErrors[bucket] += 1;
            }
        }

        var totalEvents = bucketStarts.ToDictionary(bucket => bucket, _ => 0d);
        var warningEvents = bucketStarts.ToDictionary(bucket => bucket, _ => 0d);
        var criticalEvents = bucketStarts.ToDictionary(bucket => bucket, _ => 0d);

        foreach (var row in eventRows)
        {
            var bucket = AlignDown(row.TimestampUtc, range.BucketSize);

            if (!totalEvents.ContainsKey(bucket))
            {
                continue;
            }

            totalEvents[bucket] += 1;

            if (string.Equals(row.Level, "warning", StringComparison.OrdinalIgnoreCase))
            {
                warningEvents[bucket] += 1;
            }
            else if (
                string.Equals(row.Level, "critical", StringComparison.OrdinalIgnoreCase)
                || string.Equals(row.Level, "fatal", StringComparison.OrdinalIgnoreCase)
            )
            {
                criticalEvents[bucket] += 1;
            }
        }

        return new MonitoringTrendsSnapshot(
            range.TimeRange,
            range.Label,
            fromUtc,
            toUtcExclusive,
            [
                CreateSeries(
                    "All requests",
                    "#2563eb",
                    bucketStarts,
                    totalRequests,
                    range.LabelFormat
                ),
                CreateSeries(
                    "Client errors",
                    "#f59e0b",
                    bucketStarts,
                    clientErrors,
                    range.LabelFormat
                ),
                CreateSeries(
                    "Server errors",
                    "#dc2626",
                    bucketStarts,
                    serverErrors,
                    range.LabelFormat
                ),
            ],
            [
                CreateSeries("All events", "#7c3aed", bucketStarts, totalEvents, range.LabelFormat),
                CreateSeries("Warnings", "#f59e0b", bucketStarts, warningEvents, range.LabelFormat),
                CreateSeries(
                    "Critical / fatal",
                    "#111827",
                    bucketStarts,
                    criticalEvents,
                    range.LabelFormat
                ),
            ]
        );
    }

    private async Task MaybePruneExpiredDataAsync(
        MonitoringDbContext db,
        CancellationToken cancellationToken
    )
    {
        if (_retentionDays <= 0)
        {
            return;
        }

        var now = DateTimeOffset.UtcNow;
        lock (PruneLock)
        {
            if ((now - _lastPruneAtUtc) < TimeSpan.FromMinutes(5))
            {
                return;
            }

            _lastPruneAtUtc = now;
        }

        var cutoff = now.AddDays(-_retentionDays);
        await db
            .ErrorEvents.Where(x => x.ReceivedAtUtc < cutoff)
            .ExecuteDeleteAsync(cancellationToken);
        await db
            .RequestTraces.Where(x => x.TimestampUtc < cutoff)
            .ExecuteDeleteAsync(cancellationToken);
    }

    private static int NormalizeLimit(int? maxItems, int fallback)
    {
        var limit = maxItems ?? fallback;
        return Math.Clamp(limit, 1, 1_000);
    }

    private static MonitoringChartSeries CreateSeries(
        string name,
        string color,
        IReadOnlyList<DateTimeOffset> bucketStarts,
        IReadOnlyDictionary<DateTimeOffset, double> values,
        string labelFormat
    )
    {
        return new MonitoringChartSeries(
            name,
            color,
            bucketStarts
                .Select(bucket => new MonitoringChartPoint(
                    bucket,
                    bucket.ToLocalTime().ToString(labelFormat),
                    values.GetValueOrDefault(bucket)
                ))
                .ToList()
        );
    }

    private static IReadOnlyList<DateTimeOffset> BuildBucketStarts(
        DateTimeOffset fromUtc,
        DateTimeOffset toUtcExclusive,
        TimeSpan bucketSize
    )
    {
        var buckets = new List<DateTimeOffset>();

        for (var cursor = fromUtc; cursor < toUtcExclusive; cursor = cursor.Add(bucketSize))
        {
            buckets.Add(cursor);
        }

        return buckets;
    }

    private static DateTimeOffset AlignDown(DateTimeOffset value, TimeSpan bucketSize)
    {
        var ticks = value.UtcDateTime.Ticks / bucketSize.Ticks * bucketSize.Ticks;
        return new DateTimeOffset(ticks, TimeSpan.Zero);
    }

    private static MonitoringTrendRange GetTrendRange(MonitoringTimeRange timeRange)
    {
        var normalized = Enum.IsDefined(timeRange) ? timeRange : MonitoringTimeRange.Last24Hours;

        return normalized switch
        {
            MonitoringTimeRange.Last5Minutes => new MonitoringTrendRange(
                normalized,
                "Last 5 minutes",
                TimeSpan.FromMinutes(5),
                TimeSpan.FromSeconds(30),
                "mm:ss"
            ),
            MonitoringTimeRange.LastHour => new MonitoringTrendRange(
                normalized,
                "Last hour",
                TimeSpan.FromHours(1),
                TimeSpan.FromMinutes(5),
                "HH:mm"
            ),
            MonitoringTimeRange.Last6Hours => new MonitoringTrendRange(
                normalized,
                "Last 6 hours",
                TimeSpan.FromHours(6),
                TimeSpan.FromMinutes(15),
                "HH:mm"
            ),
            MonitoringTimeRange.Last7Days => new MonitoringTrendRange(
                normalized,
                "Last 7 days",
                TimeSpan.FromDays(7),
                TimeSpan.FromHours(6),
                "dd MMM HH:mm"
            ),
            _ => new MonitoringTrendRange(
                MonitoringTimeRange.Last24Hours,
                "Last 24 hours",
                TimeSpan.FromHours(24),
                TimeSpan.FromHours(1),
                "HH:mm"
            ),
        };
    }

    private static MonitoringEventListItem Map(ErrorEvent errorEvent)
    {
        return new MonitoringEventListItem(
            errorEvent.EventId,
            errorEvent.Service,
            errorEvent.Environment,
            errorEvent.Level,
            errorEvent.Message,
            errorEvent.ExceptionType,
            errorEvent.StackTrace,
            errorEvent.RequestMethod,
            errorEvent.RequestPath,
            errorEvent.TraceId,
            errorEvent.UserId,
            errorEvent.Fingerprint,
            errorEvent.OccurredAtUtc,
            errorEvent.ReceivedAtUtc,
            DeserializeTags(errorEvent.TagsJson),
            errorEvent.AdditionalDataJson
        );
    }

    private static MonitoringRequestListItem Map(RequestTrace trace)
    {
        return new MonitoringRequestListItem(
            trace.TraceEntryId,
            trace.Method,
            trace.Path,
            trace.QueryString,
            trace.StatusCode,
            trace.DurationMs,
            trace.IsSuccess,
            trace.TraceId,
            trace.UserId,
            trace.RemoteIp,
            trace.UserAgent,
            trace.ExceptionType,
            trace.ExceptionMessage,
            trace.TimestampUtc
        );
    }

    private static IReadOnlyDictionary<string, string> DeserializeTags(string? tagsJson)
    {
        if (string.IsNullOrWhiteSpace(tagsJson))
        {
            return new Dictionary<string, string>();
        }

        return JsonSerializer.Deserialize<Dictionary<string, string>>(tagsJson, SerializerOptions)
            ?? new Dictionary<string, string>();
    }

    private static string? SerializeTags(IReadOnlyDictionary<string, string>? tags)
    {
        if (tags is null || tags.Count == 0)
        {
            return null;
        }

        var normalizedTags = tags.Where(tag =>
                !string.IsNullOrWhiteSpace(tag.Key) && !string.IsNullOrWhiteSpace(tag.Value)
            )
            .ToDictionary(
                tag => tag.Key.Trim(),
                tag => tag.Value.Trim(),
                StringComparer.OrdinalIgnoreCase
            );

        return normalizedTags.Count == 0
            ? null
            : JsonSerializer.Serialize(normalizedTags, SerializerOptions);
    }

    private static string? SerializeJson(JsonElement? additionalData)
    {
        if (
            !additionalData.HasValue
            || additionalData.Value.ValueKind == JsonValueKind.Null
            || additionalData.Value.ValueKind == JsonValueKind.Undefined
        )
        {
            return null;
        }

        return JsonSerializer.Serialize(additionalData.Value, SerializerOptions);
    }

    private static string NormalizeRequired(string? value, string fallback, int maxLength)
    {
        return TrimToLength(string.IsNullOrWhiteSpace(value) ? fallback : value.Trim(), maxLength);
    }

    private static string? NormalizeOptional(string? value, int? maxLength = null)
    {
        if (string.IsNullOrWhiteSpace(value))
        {
            return null;
        }

        var normalized = value.Trim();
        return maxLength.HasValue ? TrimToLength(normalized, maxLength.Value) : normalized;
    }

    private static string TrimToLength(string value, int maxLength)
    {
        return value.Length <= maxLength ? value : value[..maxLength];
    }

    private sealed record MonitoringTrendRange(
        MonitoringTimeRange TimeRange,
        string Label,
        TimeSpan Duration,
        TimeSpan BucketSize,
        string LabelFormat
    );

    private sealed record RequestTrendRow(DateTimeOffset TimestampUtc, int StatusCode);

    private sealed record EventTrendRow(DateTimeOffset TimestampUtc, string Level);
}
