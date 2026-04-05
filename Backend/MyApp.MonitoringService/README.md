# MyApp.MonitoringService

Internal monitoring dashboard for Durrex services with PostgreSQL-backed error collection and HTTP request tracing.

## Features

- `POST /api/events` to ingest error events from other services
- PostgreSQL-backed persistence using EF Core + Npgsql
- Automatic request trace logging via middleware
- Dashboard pages:
  - `/` or `/events` for exceptions and error events
  - `/requests` for HTTP request traces, durations, and response codes
- Protected monitoring APIs using `X-Internal-Api-Key`
- Retention pruning for old events and traces

## Configuration

Settings live in `appsettings.json`:

- `ConnectionStrings:Default` - PostgreSQL connection string
- `InternalApiKey` - required for the JSON APIs
- `Monitoring:RetentionDays` - automatic pruning window
- `Monitoring:MaxEventsOnDashboard` - max error events returned to dashboard/API
- `Monitoring:MaxRequestsOnDashboard` - max request traces returned to dashboard/API
- `Monitoring:RequestTracing:Enabled` - enable/disable request tracing middleware
- `Monitoring:RequestTracing:CaptureSuccessResponses` - whether to store successful requests too

## Error event example

```powershell
$headers = @{ "X-Internal-Api-Key" = "dev-internal-api-key-12345" }

$body = @'
{
  "service": "core-service",
  "environment": "Development",
  "level": "critical",
  "message": "Transfer creation failed with NullReferenceException",
  "exceptionType": "System.NullReferenceException",
  "stackTrace": "at Transfers.CreateAsync()",
  "requestMethod": "POST",
  "requestPath": "/api/transfers",
  "traceId": "trace-123",
  "userId": "42",
  "tags": {
    "feature": "transfers",
    "release": "2026.04.05"
  },
  "additionalData": {
    "correlationId": "corr-123",
    "accountId": 1001
  }
}
'@

Invoke-RestMethod -Method Post -Uri "http://localhost:5230/api/events" -Headers $headers -ContentType "application/json" -Body $body
```

## Request trace API examples

```powershell
$headers = @{ "X-Internal-Api-Key" = "dev-internal-api-key-12345" }

Invoke-RestMethod -Uri "http://localhost:5230/api/requests" -Headers $headers | ConvertTo-Json -Depth 6
Invoke-RestMethod -Uri "http://localhost:5230/api/requests/summary" -Headers $headers | ConvertTo-Json -Depth 6
```

## Docker Compose

The service is configured to run with the shared `postgres` container using database `durrex_monitoring`.

Open the UI directly on the monitoring service port after startup.

