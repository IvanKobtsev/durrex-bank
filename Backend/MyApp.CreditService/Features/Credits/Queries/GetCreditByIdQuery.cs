using MediatR;
using MyApp.CreditService.DTOs.Credits;

public record GetCreditByIdQuery(int CreditId) : IRequest<CreditDetailResponse>;
