using MediatR;
using Microsoft.AspNetCore.Mvc;
using MyApp.CreditService.DTOs.Credits;

namespace MyApp.CreditService.Controllers;

/// <summary>Credit lifecycle — issue, view and repay loans</summary>
[ApiController]
[Route("credits")]
[Produces("application/json")]
public class CreditsController(IMediator mediator) : ControllerBase
{
    /// <summary>Issue a new loan to a client</summary>
    /// <remarks>
    /// Requires Client role.
    /// Gateway injects X-User-Id and X-User-Role headers.
    /// </remarks>
    /// <response code="201">Loan issued and funds credited to the acount</response>
    /// <response code="400">Validation error or account not found in CoreService</response>
    [HttpPost]
    [ProducesResponseType(typeof(CreditResponse), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<ActionResult<CreditResponse>> Issue([FromBody] IssueCreditRequest request) =>
        Ok(
            await mediator.Send(
                new IssueCreditCommand(
                    request.ClientId,
                    request.AccountId,
                    request.TariffId,
                    request.Amount
                )
            )
        );

    /// <summary>List credits for a client</summary>
    /// <remarks>
    /// Employees may query any clientId. Clients may only query their own ID
    /// </remarks>
    /// <param name="clientId">Client whose credits to return</param>
    /// <response code="200">List of credits returned</response>
    /// <response code="400">clientId is missing</response>
    [HttpGet]
    [ProducesResponseType(typeof(List<CreditResponse>), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<ActionResult<List<CreditResponse>>> GetByClient([FromQuery] int? clientId) =>
        Ok(await mediator.Send(new GetCreditsByClientQuery(clientId)));

    /// <summary>Get full credit details including repayment schedule</summary>
    /// <param name="id">Credit ID</param>
    /// <response code="200">Credit details returned</response>
    /// <response code="403">Client is not the owner of this credit</response>
    /// <response code="404">Credit not found</response>
    [HttpGet("{id:int}")]
    [ProducesResponseType(typeof(CreditDetailResponse), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<CreditDetailResponse>> GetById([FromRoute] int id)
    {
        throw new NotImplementedException();
    }

    /// <summary>Fully repay a credit early</summary>
    /// <remarks>
    /// Requires Client role. Debits the remaining balance from the client's account via CoreService
    /// and closes the credit.
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
    public async Task<ActionResult<CreditResponse>> Repay([FromRoute] int id)
    {
        throw new NotImplementedException();
    }
}
