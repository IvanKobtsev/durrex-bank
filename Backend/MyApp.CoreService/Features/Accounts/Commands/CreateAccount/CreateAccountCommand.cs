using MediatR;
using MyApp.CoreService.DTOs.Responses;

namespace MyApp.CoreService.Features.Accounts.Commands.CreateAccount;

public record CreateAccountCommand(int OwnerId, string Currency = "RUB")
    : IRequest<AccountResponse>;
