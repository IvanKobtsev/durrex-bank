using MediatR;
using MyApp.CoreService.DTOs.Responses;

namespace MyApp.CoreService.Features.Transactions.Commands.Deposit;

public record DepositCommand(int AccountId, decimal Amount, string? Description)
    : IRequest<TransactionResponse>;
