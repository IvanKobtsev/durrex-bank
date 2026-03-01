using MediatR;
using Microsoft.EntityFrameworkCore;
using MyApp.CreditService.DTOs.Credits;
using MyApp.CreditService.Models;

public class RepayHandler(CreditDbContext db, ICoreServiceClient coreClient)
    : IRequestHandler<RepayCommand, CreditResponse>
{
    // Distributed transaction problem - user can be charged with money, but their credit won't close if service crashes or something.
    // Outbox pattern could be used here to fix this.
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

        await coreClient.DebitAsync(
            credit.AccountId,
            credit.RemainingBalance,
            "Early repayment",
            ct
        );

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
