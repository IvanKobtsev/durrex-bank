using MediatR;
using MyApp.CoreService.DTOs.Responses;

namespace MyApp.CoreService.Features.Transactions.Queries.GetTransactions;

public record GetTransactionsQuery(int AccountId, int Page, int PageSize)
    : IRequest<PagedResponse<TransactionResponse>>;
