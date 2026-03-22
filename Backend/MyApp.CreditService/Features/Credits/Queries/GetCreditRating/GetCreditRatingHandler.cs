using MediatR;
using Microsoft.EntityFrameworkCore;
using MyApp.CreditService.Services;

public class GetCreditRatingHandler(CreditDbContext db)
    : IRequestHandler<GetCreditRatingQuery, CreditRatingResponse>
{
    public async Task<CreditRatingResponse> Handle(GetCreditRatingQuery request, CancellationToken ct)
    {
        var credits = await db.Credits
            .Include(c => c.Schedule)
            .Where(c => c.ClientId == request.ClientId)
            .ToListAsync(ct);

        var score = CreditRatingCalculator.Calculate(credits);
        return new CreditRatingResponse(request.ClientId, score, DateTime.UtcNow);
    }
}
