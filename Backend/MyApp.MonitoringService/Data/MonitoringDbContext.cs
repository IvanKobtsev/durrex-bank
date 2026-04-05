using Microsoft.EntityFrameworkCore;
using MyApp.MonitoringService.Models;

namespace MyApp.MonitoringService.Data;

public sealed class MonitoringDbContext(DbContextOptions<MonitoringDbContext> options)
    : DbContext(options)
{
    public DbSet<ErrorEvent> ErrorEvents => Set<ErrorEvent>();
    public DbSet<RequestTrace> RequestTraces => Set<RequestTrace>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        var errorEvent = modelBuilder.Entity<ErrorEvent>();

        errorEvent.ToTable("error_events");
        errorEvent.HasKey(x => x.Id);
        errorEvent.HasIndex(x => x.EventId).IsUnique();
        errorEvent.HasIndex(x => x.ReceivedAtUtc);
        errorEvent.HasIndex(x => new { x.Service, x.ReceivedAtUtc });
        errorEvent.HasIndex(x => x.Fingerprint);

        errorEvent.Property(x => x.EventId).IsRequired().HasMaxLength(64);
        errorEvent.Property(x => x.Service).IsRequired().HasMaxLength(120);
        errorEvent.Property(x => x.Environment).IsRequired().HasMaxLength(80);
        errorEvent.Property(x => x.Level).IsRequired().HasMaxLength(32);
        errorEvent.Property(x => x.Message).IsRequired().HasMaxLength(4000);
        errorEvent.Property(x => x.ExceptionType).HasMaxLength(500);
        errorEvent.Property(x => x.RequestMethod).HasMaxLength(16);
        errorEvent.Property(x => x.RequestPath).HasMaxLength(2048);
        errorEvent.Property(x => x.TraceId).HasMaxLength(128);
        errorEvent.Property(x => x.UserId).HasMaxLength(128);
        errorEvent.Property(x => x.Fingerprint).IsRequired().HasMaxLength(300);

        var requestTrace = modelBuilder.Entity<RequestTrace>();

        requestTrace.ToTable("request_traces");
        requestTrace.HasKey(x => x.Id);
        requestTrace.HasIndex(x => x.TraceEntryId).IsUnique();
        requestTrace.HasIndex(x => x.TimestampUtc);
        requestTrace.HasIndex(x => new { x.Path, x.TimestampUtc });
        requestTrace.HasIndex(x => x.StatusCode);
        requestTrace.HasIndex(x => x.IsSuccess);

        requestTrace.Property(x => x.TraceEntryId).IsRequired().HasMaxLength(64);
        requestTrace.Property(x => x.Method).IsRequired().HasMaxLength(16);
        requestTrace.Property(x => x.Path).IsRequired().HasMaxLength(2048);
        requestTrace.Property(x => x.QueryString).HasMaxLength(4096);
        requestTrace.Property(x => x.TraceId).HasMaxLength(128);
        requestTrace.Property(x => x.UserId).HasMaxLength(128);
        requestTrace.Property(x => x.RemoteIp).HasMaxLength(64);
        requestTrace.Property(x => x.UserAgent).HasMaxLength(500);
        requestTrace.Property(x => x.ExceptionType).HasMaxLength(500);
        requestTrace.Property(x => x.ExceptionMessage).HasMaxLength(2000);
    }
}
