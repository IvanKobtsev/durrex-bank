using MediatR;
using MyApp.CreditService.DTOs.Credits;
using MyApp.CreditService.Models;

public class IssueCreditHandler(CreditDbContext db)
    : IRequestHandler<IssueCreditCommand, CreditResponse>
{
    public async Task<CreditResponse> Handle(
        IssueCreditCommand request,
        CancellationToken cancellationToken
    )
    {
        var credit = new Credit
        {
            ClientId = request.ClientId,
            AccountId = request.AccountId,
            TariffId = request.TariffId,
            Amount = request.Amount,
        };
        db.Credits.Add(credit);
        await db.SaveChangesAsync(cancellationToken);
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
