using MediatR;
using MyApp.CoreService.DTOs.Responses;

namespace MyApp.CoreService.Features.Accounts.Queries.GetAccountsByOwnerId;

public record GetAccountsByOwnerIdQuery(int OwnerId) : IRequest<IReadOnlyList<AccountResponse>>;
