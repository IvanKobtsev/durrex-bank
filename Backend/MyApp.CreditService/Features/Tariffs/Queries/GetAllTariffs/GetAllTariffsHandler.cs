using MediatR;
using Microsoft.EntityFrameworkCore;
using MyApp.CreditService.DTOs.Tariffs;

public class GetAllTariffsHandler(CreditDbContext db)
    : IRequestHandler<GetAllTariffsQuery, List<TariffResponse>>
{
    public async Task<List<TariffResponse>> Handle(
        GetAllTariffsQuery request,
        CancellationToken cancellationToken
    )
    {
        return await db
            .Tariffs.Select(t => new TariffResponse(t.Id, t.Name, t.InterestRate, t.TermMonths))
            .ToListAsync();
    }
}
