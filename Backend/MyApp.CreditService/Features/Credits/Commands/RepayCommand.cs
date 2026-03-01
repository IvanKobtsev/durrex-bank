using MediatR;
using MyApp.CreditService.DTOs.Credits;

public record RepayCommand(int CreditId) : IRequest<CreditResponse>;
