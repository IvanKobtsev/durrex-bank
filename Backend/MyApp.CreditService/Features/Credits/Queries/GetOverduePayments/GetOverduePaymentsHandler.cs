using MediatR;
using Microsoft.EntityFrameworkCore;
using MyApp.CreditService.DTOs.Credits;

public class GetOverduePaymentsHandler(CreditDbContext db)
    : IRequestHandler<GetOverduePaymentsQuery, IReadOnlyList<OverduePaymentResponse>>
{
    public async Task<IReadOnlyList<OverduePaymentResponse>> Handle(
        GetOverduePaymentsQuery request,
        CancellationToken ct)
    {
        var now = DateTime.UtcNow;
        var entries = await db.PaymentScheduleEntries
            .AsNoTracking()
            .Include(e => e.Credit)
            .Where(e => !e.IsPaid
                     && e.DueDate < now
                     && e.Credit.ClientId == request.ClientId)
            .ToListAsync(ct);

        return entries
            .Select(e => new OverduePaymentResponse(
                e.Id,
                e.CreditId,
                e.DueDate,
                e.Amount,
                (now - e.DueDate).Days))
            .ToList();
    }
}
