using MediatR;

namespace MyApp.CoreService.Features.Notifications.Commands;

public class Subscribe : IRequest
{
    public required string FireBaseToken { get; set; }
}
