using MediatR;
using MyApp.CreditService.DTOs.Tariffs;
using MyApp.CreditService.Models;

public class CreateTariffHandler(CreditDbContext db)
    : IRequestHandler<CreateTariffCommand, TariffResponse>
{
    public async Task<TariffResponse> Handle(CreateTariffCommand request, CancellationToken ct)
    {
        var tariff = new Tariff
        {
            Name = request.Name,
            InterestRate = request.InterestRate,
            TermMonths = request.TermMonths,
        };
        db.Tariffs.Add(tariff);
        await db.SaveChangesAsync(ct);
        return new TariffResponse(tariff.Id, tariff.Name, tariff.InterestRate, tariff.TermMonths);
    }
}
