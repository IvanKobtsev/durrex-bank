using MediatR;
using Microsoft.EntityFrameworkCore;
using MyApp.CreditService.DTOs.Credits;

public class GetCreditsByClientHandler(CreditDbContext db)
    : IRequestHandler<GetCreditsByClientQuery, List<CreditResponse>>
{
    public async Task<List<CreditResponse>> Handle(
        GetCreditsByClientQuery request,
        CancellationToken cancellationToken
    )
    {
        var credits = await db
            .Credits.Where(c => c.ClientId == request.ClientId)
            .Include(c => c.Tariff)
            .ToListAsync(cancellationToken);

        return credits
            .Select(c => new CreditResponse(
                c.Id,
                c.ClientId,
                c.AccountId,
                c.Tariff.Name,
                c.Amount,
                c.RemainingBalance,
                c.Status,
                c.IssuedAt
            ))
            .ToList();
    }
}
