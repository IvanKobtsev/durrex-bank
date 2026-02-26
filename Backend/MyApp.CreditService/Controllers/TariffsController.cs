using Microsoft.AspNetCore.Mvc;
using MyApp.CreditService.DTOs.Tariffs;

namespace MyApp.CreditService.Controllers;

/// <summary>Credit tariff management</summary>
[ApiController]
[Route("tariffs")]
[Produces("application/json")]
public class TariffsController : ControllerBase
{
    /// <summary>Get all available credit tariffs</summary>
    /// <response code="200">List of tariffs returned</response>
    [HttpGet]
    [ProducesResponseType(typeof(List<TariffResponse>), StatusCodes.Status200OK)]
    public async Task<ActionResult<List<TariffResponse>>> GetAll()
    {
        throw new NotImplementedException();
    }

    /// <summary>Create a new credit tariff</summary>
    /// <remarks>Requires Employee role</remarks>
    /// <response code="201">Tariff created successfully</response>
    /// <response code="400">Validation error</response>
    [HttpPost]
    [ProducesResponseType(typeof(TariffResponse), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<ActionResult<TariffResponse>> Create([FromBody] CreateTariffRequest request)
    {
        throw new NotImplementedException();
    }
}
