using MediatR;
using MyApp.CreditService.DTOs.Tariffs;

public record CreateTariffCommand(string Name, decimal InterestRate, int TermMonths)
    : IRequest<TariffResponse>;
