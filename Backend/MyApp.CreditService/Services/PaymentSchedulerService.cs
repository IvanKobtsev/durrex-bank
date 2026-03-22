using MassTransit;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Messaging.Messages;
using MyApp.CreditService.Models;

namespace MyApp.CreditService.Services;

public class PaymentSchedulerService(
    IServiceScopeFactory scopeFactory,
    IConfiguration config,
    ILogger<PaymentSchedulerService> logger
) : BackgroundService
{
    protected override async Task ExecuteAsync(CancellationToken ct)
    {
        while (!ct.IsCancellationRequested)
        {
            await ProcessDuePaymentsAsync(ct);
            await Task.Delay(TimeSpan.FromMinutes(1), ct);
        }
    }

    private async Task ProcessDuePaymentsAsync(CancellationToken ct)
    {
        await using var scope = scopeFactory.CreateAsyncScope();
        var db = scope.ServiceProvider.GetRequiredService<CreditDbContext>();
        var publishEndpoint = scope.ServiceProvider.GetRequiredService<IPublishEndpoint>();

        var masterAccountId = config.GetValue<int>("Bank:MasterAccountId");
        var now = DateTime.UtcNow;
        var dueEntries = await db
            .PaymentScheduleEntries.Where(e => !e.IsPaid && e.DueDate <= now)
            .Include(e => e.Credit)
                .ThenInclude(c => c.Schedule)
            .ToListAsync(ct);

        foreach (var entry in dueEntries)
        {
            try
            {
                await publishEndpoint.Publish(
                    new TransactionRequested(
                        MessageId: Guid.NewGuid(),
                        AccountId: entry.Credit.AccountId,
                        Type: TransactionType.CreditRepayment,
                        Amount: entry.Amount,
                        RelatedAccountId: masterAccountId,
                        Description: "Scheduled payment",
                        RequestedByUserId: null
                    ),
                    ct);

                entry.IsPaid = true;
                entry.PaidAt = now;
                entry.Credit.RemainingBalance -= entry.Amount;

                if (entry.Credit.Schedule.All(e => e.IsPaid))
                {
                    entry.Credit.Status = CreditStatus.Closed;
                    entry.Credit.ClosedAt = now;
                }

                await db.SaveChangesAsync(ct);
            }
            catch (Exception ex)
            {
                logger.LogWarning(
                    ex,
                    "Failed to process scheduled payment for entry {EntryId} (credit {CreditId})",
                    entry.Id,
                    entry.CreditId
                );
            }
        }
    }
}
