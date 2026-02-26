using MediatR;
using MyApp.CoreService.DTOs.Responses;

namespace MyApp.CoreService.Features.Accounts.Queries.GetAllAccounts;

public record GetAllAccountsQuery : IRequest<IReadOnlyList<AccountResponse>>;
