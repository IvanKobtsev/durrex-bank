using MassTransit;
using MediatR;
using MyApp.CoreService.DTOs.Responses;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Messaging.Messages;

namespace MyApp.CoreService.Features.Transactions.Commands.Transfer;

public class TransferHandler(IRequestClient<TransactionRequested> client)
    : IRequestHandler<TransferCommand, TransactionResponse>
{
    public async Task<TransactionResponse> Handle(TransferCommand cmd, CancellationToken ct)
    {
        if (cmd.Amount <= 0)
            throw new ArgumentException("Amount must be positive.");

        if (cmd.SourceAccountId == cmd.TargetAccountId)
            throw new ArgumentException("Cannot transfer to the same account.");

        var response = await client.GetResponse<TransactionCompleted>(
            new TransactionRequested(
                MessageId: Guid.NewGuid(),
                AccountId: cmd.SourceAccountId,
                Type: TransactionType.Transfer,
                Amount: cmd.Amount,
                RelatedAccountId: cmd.TargetAccountId,
                Description: cmd.Description,
                RequestedByUserId: null
            ), ct);

        return response.Message.Transaction;
    }
}
