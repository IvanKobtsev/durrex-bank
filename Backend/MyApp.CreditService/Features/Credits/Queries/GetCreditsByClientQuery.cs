using MediatR;
using MyApp.CreditService.DTOs.Credits;

public record GetCreditsByClientQuery(int? ClientId) : IRequest<List<CreditResponse>>;
