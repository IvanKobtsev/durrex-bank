using MassTransit;
using MediatR;
using MyApp.CoreService.DTOs.Responses;
using MyApp.CoreService.Enums;
using MyApp.CoreService.Messaging.Messages;

namespace MyApp.CoreService.Features.Transactions.Commands.Deposit;

public class DepositHandler(IRequestClient<TransactionRequested> client)
    : IRequestHandler<DepositCommand, TransactionResponse>
{
    public async Task<TransactionResponse> Handle(DepositCommand cmd, CancellationToken ct)
    {
        if (cmd.Amount <= 0)
            throw new ArgumentException("Amount must be positive.");

        var response = await client.GetResponse<TransactionCompleted>(
            new TransactionRequested(
                MessageId: Guid.NewGuid(),
                AccountId: cmd.AccountId,
                Type: TransactionType.Deposit,
                Amount: cmd.Amount,
                RelatedAccountId: null,
                Description: cmd.Description,
                RequestedByUserId: null
            ), ct);

        return response.Message.Transaction;
    }
}
