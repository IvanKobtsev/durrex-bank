using MediatR;
using MyApp.CoreService.Data;
using MyApp.CoreService.DTOs.Responses;
using MyApp.CoreService.Features.Accounts.Commands.CreateAccount;

namespace MyApp.CoreService.Features.Accounts.Queries.GetAccountById;

public class GetAccountByIdHandler : IRequestHandler<GetAccountByIdQuery, AccountResponse>
{
    private readonly CoreDbContext _db;

    public GetAccountByIdHandler(CoreDbContext db) => _db = db;

    public async Task<AccountResponse> Handle(GetAccountByIdQuery query, CancellationToken ct)
    {
        var account = await _db.Accounts.FindAsync([query.AccountId], ct)
            ?? throw new KeyNotFoundException($"Account {query.AccountId} not found.");

        return CreateAccountHandler.Map(account);
    }
}
