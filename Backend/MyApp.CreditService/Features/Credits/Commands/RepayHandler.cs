using MassTransit;
using MediatR;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Messaging.Messages;
using MyApp.CreditService.Auth;
using MyApp.CreditService.DTOs.Credits;
using MyApp.CreditService.Models;

public class RepayHandler(
    CreditDbContext db,
    IPublishEndpoint publishEndpoint,
    IConfiguration config,
    ICurrentUserContext user
) : IRequestHandler<RepayCommand, CreditResponse>
{
    public async Task<CreditResponse> Handle(RepayCommand request, CancellationToken ct)
    {
        var credit =
            await db
                .Credits.Include(c => c.Tariff)
                .Include(c => c.Schedule)
                .FirstOrDefaultAsync(c => c.Id == request.CreditId, ct)
            ?? throw new KeyNotFoundException($"Credit {request.CreditId} not found.");

        if (credit.Status != CreditStatus.Active)
            throw new InvalidOperationException("Only active credits can be repaid.");

        if (user.IsClient && credit.ClientId != user.UserId)
            throw new UnauthorizedAccessException("Clients can only repay their own credits.");

        var masterAccountId = config.GetValue<int>("Bank:MasterAccountId");

        await publishEndpoint.Publish(
            new TransactionRequested(
                MessageId: Guid.NewGuid(),
                AccountId: credit.AccountId,
                Type: TransactionType.CreditRepayment,
                Amount: credit.RemainingBalance,
                RelatedAccountId: masterAccountId,
                Description: "Early repayment",
                RequestedByUserId: null
            ),
            ct);

        var now = DateTime.UtcNow;
        foreach (var entry in credit.Schedule.Where(e => !e.IsPaid))
        {
            entry.IsPaid = true;
            entry.PaidAt = now;
        }

        credit.RemainingBalance = 0;
        credit.Status = CreditStatus.Closed;
        credit.ClosedAt = now;

        await db.SaveChangesAsync(ct);

        return new CreditResponse(
            credit.Id,
            credit.ClientId,
            credit.AccountId,
            credit.Tariff.Name,
            credit.Amount,
            credit.RemainingBalance,
            credit.Status,
            credit.IssuedAt
        );
    }
}
