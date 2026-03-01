using MediatR;
using MyApp.CreditService.DTOs.Credits;
using MyApp.CreditService.Models;

public class IssueCreditHandler(CreditDbContext db, ICoreServiceClient coreClient)
    : IRequestHandler<IssueCreditCommand, CreditResponse>
{
    public async Task<CreditResponse> Handle(IssueCreditCommand request, CancellationToken ct)
    {
        var tariff = await db.Tariffs.FindAsync([request.TariffId], ct)
            ?? throw new KeyNotFoundException($"Tariff {request.TariffId} not found.");

        var monthlyRate = tariff.InterestRate / 12;
        var payment = Math.Round(
            request.Amount * monthlyRate
                / (1 - (decimal)Math.Pow((double)(1 + monthlyRate), -tariff.TermMonths)),
            2);

        var issuedAt = DateTime.UtcNow;
        var credit = new Credit
        {
            ClientId = request.ClientId,
            AccountId = request.AccountId,
            TariffId = request.TariffId,
            Amount = request.Amount,
            RemainingBalance = payment * tariff.TermMonths,
            Status = CreditStatus.Active,
            IssuedAt = issuedAt,
            Schedule = Enumerable.Range(1, tariff.TermMonths)
                .Select(i => new PaymentScheduleEntry
                {
                    DueDate = issuedAt.AddMonths(i),
                    Amount = payment,
                })
                .ToList()
        };

        db.Credits.Add(credit);
        await db.SaveChangesAsync(ct);

        await coreClient.DepositAsync(request.AccountId, request.Amount, "Credit issuance", ct);

        return new CreditResponse(
            credit.Id,
            credit.ClientId,
            credit.AccountId,
            tariff.Name,
            credit.Amount,
            credit.RemainingBalance,
            credit.Status,
            credit.IssuedAt
        );
    }
}
