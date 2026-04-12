using MediatR;
using Microsoft.AspNetCore.Mvc;
using MyApp.CoreService.Auth;
using MyApp.CoreService.Features.Notifications.Commands;

namespace MyApp.CoreService.Controllers;

[ApiController]
[Route("api/push-notifications")]
[Produces("application/json")]
public class NotificationsController(ICurrentUserContext currentUserContext, IMediator mediator)
    : ControllerBase
{
    [HttpPost("subscribe")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> Subscribe([FromBody] Subscribe cmd, CancellationToken ct)
    {
        if (currentUserContext is { IsClient: true, UserId: null })
            return Unauthorized(new { error = "X-User-Id header is missing or invalid." });

        await mediator.Send(cmd, ct);
        return Ok();
    }
}
