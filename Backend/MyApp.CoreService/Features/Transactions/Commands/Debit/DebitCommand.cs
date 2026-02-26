using MediatR;
using MyApp.CoreService.DTOs.Responses;

namespace MyApp.CoreService.Features.Transactions.Commands.Debit;

public record DebitCommand(int AccountId, decimal Amount, string? Description)
    : IRequest<TransactionResponse>;
