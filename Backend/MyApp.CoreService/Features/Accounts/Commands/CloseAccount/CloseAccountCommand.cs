using MediatR;
using MyApp.CoreService.DTOs.Responses;

namespace MyApp.CoreService.Features.Accounts.Commands.CloseAccount;

public record CloseAccountCommand(int AccountId) : IRequest<AccountResponse>;
