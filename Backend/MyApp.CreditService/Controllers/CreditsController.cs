using MediatR;
using Microsoft.AspNetCore.Mvc;
using MyApp.CreditService.Auth;
using MyApp.CreditService.DTOs.Credits;

namespace MyApp.CreditService.Controllers;

/// <summary>Credit lifecycle — issue, view and repay loans</summary>
[ApiController]
[Route("credits")]
[Produces("application/json")]
public class CreditsController(IMediator mediator, ICurrentUserContext currentUser) : ControllerBase
{
    /// <summary>Issue a new loan to a client</summary>
    /// <remarks>
    /// Clients may only issue a loan for themselves — ClientId is forced to X-User-Id.
    /// Employees may issue a loan on behalf of any client.
    /// </remarks>
    /// <response code="201">Loan issued and funds credited to the account</response>
    /// <response code="400">Validation error or account not found in CoreService</response>
    /// <response code="403">Only clients and employees may issue loans</response>
    [HttpPost]
    [ProducesResponseType(typeof(CreditResponse), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<ActionResult<CreditResponse>> Issue(
        [FromBody] IssueCreditRequest request,
        CancellationToken ct
    )
    {
        int clientId;

        if (currentUser.IsClient)
        {
            if (currentUser.UserId is null)
                return BadRequest(new { error = "X-User-Id header is missing or invalid." });
            clientId = currentUser.UserId.Value;
        }
        else if (currentUser.IsEmployee)
        {
            clientId = request.ClientId;
        }
        else
        {
            return StatusCode(StatusCodes.Status403Forbidden);
        }

        var credit = await mediator.Send(
            new IssueCreditCommand(clientId, request.AccountId, request.TariffId, request.Amount),
            ct
        );

        return CreatedAtAction(nameof(GetById), new { id = credit.Id }, credit);
    }

    /// <summary>List credits for a client</summary>
    /// <remarks>
    /// Clients may only query their own credits — clientId is forced to X-User-Id.
    /// Employees must supply a clientId query parameter.
    /// </remarks>
    /// <param name="clientId">Client whose credits to return (required for employees)</param>
    /// <response code="200">List of credits returned</response>
    /// <response code="400">clientId is missing (employees only)</response>
    /// <response code="403">Only clients and employees may list credits</response>
    [HttpGet]
    [ProducesResponseType(typeof(List<CreditResponse>), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<ActionResult<List<CreditResponse>>> GetByClient(
        [FromQuery] int? clientId,
        CancellationToken ct
    )
    {
        int resolvedClientId;

        if (currentUser.IsClient)
        {
            if (currentUser.UserId is null)
                return BadRequest(new { error = "X-User-Id header is missing or invalid." });
            resolvedClientId = currentUser.UserId.Value;
        }
        else if (currentUser.IsEmployee)
        {
            if (clientId is null)
                return BadRequest(new { error = "clientId query parameter is required." });
            resolvedClientId = clientId.Value;
        }
        else
        {
            return StatusCode(StatusCodes.Status403Forbidden);
        }

        return Ok(await mediator.Send(new GetCreditsByClientQuery(resolvedClientId), ct));
    }

    /// <summary>Get full credit details including repayment schedule</summary>
    /// <param name="id">Credit ID</param>
    /// <response code="200">Credit details returned</response>
    /// <response code="403">Client is not the owner of this credit</response>
    /// <response code="404">Credit not found</response>
    [HttpGet("{id:int}")]
    [ProducesResponseType(typeof(CreditDetailResponse), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<CreditDetailResponse>> GetById(
        [FromRoute] int id,
        CancellationToken ct
    )
    {
        var credit = await mediator.Send(new GetCreditByIdQuery(id), ct);

        if (currentUser.IsClient && currentUser.UserId != credit.ClientId)
            return StatusCode(StatusCodes.Status403Forbidden);

        return Ok(credit);
    }

    /// <summary>Fully repay a credit early</summary>
    /// <remarks>
    /// Debits the remaining balance from the client's account via CoreService and closes the credit.
    /// Clients may only repay their own credits. Employees may repay any credit.
    /// </remarks>
    /// <param name="id">Credit ID to repay</param>
    /// <response code="200">Credit fully repaid and closed</response>
    /// <response code="400">Insufficient funds or credit is already closed</response>
    /// <response code="403">Client is not the owner of this credit</response>
    /// <response code="404">Credit not found</response>
    [HttpPost("{id:int}/repay")]
    [ProducesResponseType(typeof(CreditResponse), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<CreditResponse>> Repay(
        [FromRoute] int id,
        CancellationToken ct
    )
    {
        if (currentUser.IsClient)
        {
            var credit = await mediator.Send(new GetCreditByIdQuery(id), ct);
            if (currentUser.UserId != credit.ClientId)
                return StatusCode(StatusCodes.Status403Forbidden);
        }

        return Ok(await mediator.Send(new RepayCommand(id), ct));
    }
}
