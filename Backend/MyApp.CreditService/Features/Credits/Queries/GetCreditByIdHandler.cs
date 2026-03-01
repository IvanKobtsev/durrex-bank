using MediatR;
using Microsoft.EntityFrameworkCore;
using MyApp.CreditService.DTOs.Credits;

public class GetCreditByIdHandler(CreditDbContext db)
    : IRequestHandler<GetCreditByIdQuery, CreditDetailResponse>
{
    public async Task<CreditDetailResponse> Handle(GetCreditByIdQuery request, CancellationToken ct)
    {
        var credit = await db.Credits
            .Include(c => c.Tariff)
            .Include(c => c.Schedule)
            .FirstOrDefaultAsync(c => c.Id == request.CreditId, ct)
            ?? throw new KeyNotFoundException($"Credit {request.CreditId} not found.");

        var nextPaymentDate = credit.Schedule
            .Where(e => !e.IsPaid)
            .OrderBy(e => e.DueDate)
            .Select(e => e.DueDate)
            .Cast<DateTime?>()
            .FirstOrDefault();

        var schedule = credit.Schedule
            .OrderBy(e => e.DueDate)
            .Select(e => new PaymentScheduleEntryResponse(e.Id, e.DueDate, e.Amount, e.IsPaid, e.PaidAt))
            .ToList();

        return new CreditDetailResponse(
            credit.Id,
            credit.ClientId,
            credit.AccountId,
            credit.Tariff.Name,
            credit.Amount,
            credit.RemainingBalance,
            credit.Status,
            credit.IssuedAt,
            nextPaymentDate,
            schedule
        );
    }
}
