using MassTransit;
using MediatR;
using Microsoft.Extensions.Configuration;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Messaging.Messages;
using MyApp.CreditService.Auth;
using MyApp.CreditService.DTOs.Credits;
using MyApp.CreditService.Models;

public class IssueCreditHandler(
    CreditDbContext db,
    IPublishEndpoint publishEndpoint,
    IConfiguration config,
    ICurrentUserContext user
) : IRequestHandler<IssueCreditCommand, CreditResponse>
{
    public async Task<CreditResponse> Handle(IssueCreditCommand request, CancellationToken ct)
    {
        if (user.IsClient && request.ClientId != user.UserId)
            throw new UnauthorizedAccessException("Clients can only issue credits for themselves.");

        var tariff =
            await db.Tariffs.FindAsync([request.TariffId], ct)
            ?? throw new KeyNotFoundException($"Tariff {request.TariffId} not found.");

        // var minuteRate = tariff.InterestRate / (365 * 24 * 60);
        var minuteRate = tariff.InterestRate; // For demo purposes
        var payment = Math.Round(
            request.Amount
                * minuteRate
                / (1 - (decimal)Math.Pow((double)(1 + minuteRate), -tariff.TermMonths)),
            2
        );

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
            Schedule = Enumerable
                .Range(1, tariff.TermMonths)
                .Select(i => new PaymentScheduleEntry
                {
                    DueDate = issuedAt.AddMinutes(i),
                    Amount = payment,
                })
                .ToList(),
        };

        db.Credits.Add(credit);

        var masterAccountId = config.GetValue<int>("Bank:MasterAccountId");

        await publishEndpoint.Publish(
            new TransactionRequested(
                MessageId: Guid.NewGuid(),
                AccountId: masterAccountId,
                Type: TransactionType.Transfer,
                Amount: request.Amount,
                RelatedAccountId: request.AccountId,
                Description: "Credit issuance",
                RequestedByUserId: null
            ),
            ct);

        await db.SaveChangesAsync(ct);

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
