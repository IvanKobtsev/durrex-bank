using MediatR;
using MyApp.CreditService.DTOs.Credits;

public record GetOverduePaymentsQuery(int ClientId) : IRequest<IReadOnlyList<OverduePaymentResponse>>;
