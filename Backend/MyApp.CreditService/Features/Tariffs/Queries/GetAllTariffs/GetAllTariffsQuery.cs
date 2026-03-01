using MediatR;
using MyApp.CreditService.DTOs.Tariffs;

public record GetAllTariffsQuery : IRequest<List<TariffResponse>>;
