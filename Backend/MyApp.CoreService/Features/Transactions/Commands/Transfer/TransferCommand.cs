using MediatR;
using MyApp.CoreService.DTOs.Responses;

namespace MyApp.CoreService.Features.Transactions.Commands.Transfer;

public record TransferCommand(int SourceAccountId, int TargetAccountId, decimal Amount, string? Description)
    : IRequest<(TransactionResponse Source, TransactionResponse Target)>;
