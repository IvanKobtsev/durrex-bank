using MediatR;
using MyApp.CreditService.DTOs.Credits;

public record IssueCreditCommand(int ClientId, int AccountId, int TariffId, decimal Amount)
    : IRequest<CreditResponse>;
