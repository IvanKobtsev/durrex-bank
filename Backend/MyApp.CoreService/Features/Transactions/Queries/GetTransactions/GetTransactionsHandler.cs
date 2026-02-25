using MediatR;
using Microsoft.EntityFrameworkCore;
using MyApp.CoreService.Data;
using MyApp.CoreService.DTOs.Responses;
using MyApp.CoreService.Features.Transactions.Shared;

namespace MyApp.CoreService.Features.Transactions.Queries.GetTransactions;

public class GetTransactionsHandler : IRequestHandler<GetTransactionsQuery, PagedResponse<TransactionResponse>>
{
    private readonly CoreDbContext _db;

    public GetTransactionsHandler(CoreDbContext db) => _db = db;

    public async Task<PagedResponse<TransactionResponse>> Handle(GetTransactionsQuery query, CancellationToken ct)
    {
        if (await _db.Accounts.FindAsync([query.AccountId], ct) is null)
            throw new KeyNotFoundException($"Account {query.AccountId} not found.");

        var page = Math.Max(1, query.Page);
        var pageSize = Math.Clamp(query.PageSize, 1, 100);

        var baseQuery = _db.Transactions
            .Where(t => t.AccountId == query.AccountId)
            .OrderByDescending(t => t.CreatedAt);

        var totalCount = await baseQuery.CountAsync(ct);
        var items = await baseQuery
            .Skip((page - 1) * pageSize)
            .Take(pageSize)
            .ToListAsync(ct);

        return new PagedResponse<TransactionResponse>(
            items.Select(AccountHelper.MapTransaction).ToList(),
            page,
            pageSize,
            totalCount,
            (int)Math.Ceiling((double)totalCount / pageSize));
    }
}
