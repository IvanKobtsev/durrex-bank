using MediatR;
using MyApp.CoreService.DTOs.Responses;

namespace MyApp.CoreService.Features.Accounts.Queries.GetAccountById;

public record GetAccountByIdQuery(int AccountId) : IRequest<AccountResponse>;
