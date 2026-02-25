using Microsoft.AspNetCore.Mvc;
using MyApp.UserService.DTOs;

namespace MyApp.UserService.Controllers;

/// <summary>Authentication — login and public key endpoints</summary>
[ApiController]
[Route("auth")]
[Produces("application/json")]
public class AuthController : ControllerBase
{
    /// <summary>Issue a JWT token for valid credentials</summary>
    /// <response code="200">Token issued successfully</response>
    /// <response code="401">Invalid credentials or user is blocked</response>
    [HttpPost("login")]
    [ProducesResponseType(typeof(LoginResponse), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<ActionResult<LoginResponse>> Login([FromBody] LoginRequest request)
    {
        throw new NotImplementedException();
    }

    /// <summary>Get the RSA public key used to validate issued JWTs</summary>
    /// <remarks>Gateway calls this endpoint on startup to obtain the key for token verification.</remarks>
    /// <response code="200">Public key returned</response>
    [HttpGet("public-key")]
    [ProducesResponseType(typeof(PublicKeyResponse), StatusCodes.Status200OK)]
    public ActionResult<PublicKeyResponse> GetPublicKey()
    {
        throw new NotImplementedException();
    }
}
