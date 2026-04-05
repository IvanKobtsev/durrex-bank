using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace MyApp.MonitoringService.Migrations
{
    /// <inheritdoc />
    public partial class InitialMonitoring : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "error_events",
                columns: table => new
                {
                    Id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    EventId = table.Column<string>(type: "character varying(64)", maxLength: 64, nullable: false),
                    Service = table.Column<string>(type: "character varying(120)", maxLength: 120, nullable: false),
                    Environment = table.Column<string>(type: "character varying(80)", maxLength: 80, nullable: false),
                    Level = table.Column<string>(type: "character varying(32)", maxLength: 32, nullable: false),
                    Message = table.Column<string>(type: "character varying(4000)", maxLength: 4000, nullable: false),
                    ExceptionType = table.Column<string>(type: "character varying(500)", maxLength: 500, nullable: true),
                    StackTrace = table.Column<string>(type: "text", nullable: true),
                    RequestMethod = table.Column<string>(type: "character varying(16)", maxLength: 16, nullable: true),
                    RequestPath = table.Column<string>(type: "character varying(2048)", maxLength: 2048, nullable: true),
                    TraceId = table.Column<string>(type: "character varying(128)", maxLength: 128, nullable: true),
                    UserId = table.Column<string>(type: "character varying(128)", maxLength: 128, nullable: true),
                    Fingerprint = table.Column<string>(type: "character varying(300)", maxLength: 300, nullable: false),
                    TagsJson = table.Column<string>(type: "text", nullable: true),
                    AdditionalDataJson = table.Column<string>(type: "text", nullable: true),
                    OccurredAtUtc = table.Column<DateTimeOffset>(type: "timestamp with time zone", nullable: false),
                    ReceivedAtUtc = table.Column<DateTimeOffset>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_error_events", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "request_traces",
                columns: table => new
                {
                    Id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    TraceEntryId = table.Column<string>(type: "character varying(64)", maxLength: 64, nullable: false),
                    Method = table.Column<string>(type: "character varying(16)", maxLength: 16, nullable: false),
                    Path = table.Column<string>(type: "character varying(2048)", maxLength: 2048, nullable: false),
                    QueryString = table.Column<string>(type: "character varying(4096)", maxLength: 4096, nullable: true),
                    StatusCode = table.Column<int>(type: "integer", nullable: false),
                    DurationMs = table.Column<double>(type: "double precision", nullable: false),
                    IsSuccess = table.Column<bool>(type: "boolean", nullable: false),
                    TraceId = table.Column<string>(type: "character varying(128)", maxLength: 128, nullable: true),
                    UserId = table.Column<string>(type: "character varying(128)", maxLength: 128, nullable: true),
                    RemoteIp = table.Column<string>(type: "character varying(64)", maxLength: 64, nullable: true),
                    UserAgent = table.Column<string>(type: "character varying(500)", maxLength: 500, nullable: true),
                    ExceptionType = table.Column<string>(type: "character varying(500)", maxLength: 500, nullable: true),
                    ExceptionMessage = table.Column<string>(type: "character varying(2000)", maxLength: 2000, nullable: true),
                    TimestampUtc = table.Column<DateTimeOffset>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_request_traces", x => x.Id);
                });

            migrationBuilder.CreateIndex(
                name: "IX_error_events_EventId",
                table: "error_events",
                column: "EventId",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_error_events_Fingerprint",
                table: "error_events",
                column: "Fingerprint");

            migrationBuilder.CreateIndex(
                name: "IX_error_events_ReceivedAtUtc",
                table: "error_events",
                column: "ReceivedAtUtc");

            migrationBuilder.CreateIndex(
                name: "IX_error_events_Service_ReceivedAtUtc",
                table: "error_events",
                columns: new[] { "Service", "ReceivedAtUtc" });

            migrationBuilder.CreateIndex(
                name: "IX_request_traces_IsSuccess",
                table: "request_traces",
                column: "IsSuccess");

            migrationBuilder.CreateIndex(
                name: "IX_request_traces_Path_TimestampUtc",
                table: "request_traces",
                columns: new[] { "Path", "TimestampUtc" });

            migrationBuilder.CreateIndex(
                name: "IX_request_traces_StatusCode",
                table: "request_traces",
                column: "StatusCode");

            migrationBuilder.CreateIndex(
                name: "IX_request_traces_TimestampUtc",
                table: "request_traces",
                column: "TimestampUtc");

            migrationBuilder.CreateIndex(
                name: "IX_request_traces_TraceEntryId",
                table: "request_traces",
                column: "TraceEntryId",
                unique: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "error_events");

            migrationBuilder.DropTable(
                name: "request_traces");
        }
    }
}
