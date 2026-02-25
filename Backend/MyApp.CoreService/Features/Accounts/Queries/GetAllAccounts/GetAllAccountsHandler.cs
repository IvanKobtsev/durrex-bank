using MediatR;
using Microsoft.EntityFrameworkCore;
using MyApp.CoreService.Data;
using MyApp.CoreService.DTOs.Responses;
using MyApp.CoreService.Features.Accounts.Commands.CreateAccount;

namespace MyApp.CoreService.Features.Accounts.Queries.GetAllAccounts;

public class GetAllAccountsHandler : IRequestHandler<GetAllAccountsQuery, IReadOnlyList<AccountResponse>>
{
    private readonly CoreDbContext _db;

    public GetAllAccountsHandler(CoreDbContext db) => _db = db;

    public async Task<IReadOnlyList<AccountResponse>> Handle(GetAllAccountsQuery query, CancellationToken ct)
    {
        var accounts = await _db.Accounts
            .OrderBy(a => a.Id)
            .ToListAsync(ct);

        return accounts.Select(CreateAccountHandler.Map).ToList();
    }
}
