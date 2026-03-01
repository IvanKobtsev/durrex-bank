using MediatR;
using Microsoft.AspNetCore.Mvc;
using MyApp.CreditService.Auth;
using MyApp.CreditService.DTOs.Tariffs;

namespace MyApp.CreditService.Controllers;

/// <summary>Credit tariff management</summary>
[ApiController]
[Route("tariffs")]
[Produces("application/json")]
public class TariffsController(IMediator mediator, ICurrentUserContext currentUser) : ControllerBase
{
    /// <summary>Get all available credit tariffs</summary>
    /// <response code="200">List of tariffs returned</response>
    [HttpGet]
    [ProducesResponseType(typeof(List<TariffResponse>), StatusCodes.Status200OK)]
    public async Task<ActionResult<List<TariffResponse>>> GetAll(CancellationToken ct) =>
        Ok(await mediator.Send(new GetAllTariffsQuery(), ct));

    /// <summary>Create a new credit tariff</summary>
    /// <response code="201">Tariff created successfully</response>
    /// <response code="400">Validation error</response>
    /// <response code="403">Only employees may create tariffs</response>
    [HttpPost]
    [ProducesResponseType(typeof(TariffResponse), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<ActionResult<TariffResponse>> Create(
        [FromBody] CreateTariffRequest request,
        CancellationToken ct
    )
    {
        if (!currentUser.IsEmployee)
            return Forbid();

        return Ok(
            await mediator.Send(
                new CreateTariffCommand(request.Name, request.InterestRate, request.TermMonths), ct
            )
        );
    }
}
