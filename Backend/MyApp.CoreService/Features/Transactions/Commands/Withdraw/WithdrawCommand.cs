using MediatR;
using MyApp.CoreService.DTOs.Responses;

namespace MyApp.CoreService.Features.Transactions.Commands.Withdraw;

public record WithdrawCommand(int AccountId, decimal Amount, string? Description)
    : IRequest<TransactionResponse>;
