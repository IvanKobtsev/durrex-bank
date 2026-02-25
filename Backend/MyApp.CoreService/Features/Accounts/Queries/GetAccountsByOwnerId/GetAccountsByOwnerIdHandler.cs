using MediatR;
using Microsoft.EntityFrameworkCore;
using MyApp.CoreService.Data;
using MyApp.CoreService.DTOs.Responses;
using MyApp.CoreService.Features.Accounts.Commands.CreateAccount;

namespace MyApp.CoreService.Features.Accounts.Queries.GetAccountsByOwnerId;

public class GetAccountsByOwnerIdHandler : IRequestHandler<GetAccountsByOwnerIdQuery, IReadOnlyList<AccountResponse>>
{
    private readonly CoreDbContext _db;

    public GetAccountsByOwnerIdHandler(CoreDbContext db) => _db = db;

    public async Task<IReadOnlyList<AccountResponse>> Handle(GetAccountsByOwnerIdQuery query, CancellationToken ct)
    {
        var accounts = await _db.Accounts
            .Where(a => a.OwnerId == query.OwnerId)
            .OrderBy(a => a.CreatedAt)
            .ToListAsync(ct);

        return accounts.Select(CreateAccountHandler.Map).ToList();
    }
}
