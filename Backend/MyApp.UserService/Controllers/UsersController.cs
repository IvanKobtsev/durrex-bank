using Microsoft.AspNetCore.Mvc;
using MyApp.UserService.Auth;
using MyApp.UserService.DTOs;
using MyApp.UserService.Services;
using MyApp.UserService.Services.Errors;

namespace MyApp.UserService.Controllers;

/// <summary>User management — create, read, block and unblock users</summary>
[ApiController]
[Route("users")]
[Produces("application/json")]
public class UsersController(IUserService userService, ICurrentUserContext currentUser) : ControllerBase
{
    /// <summary>Get a list of all users (clients and employees)</summary>
    /// <response code="200">List of users returned</response>
    /// <response code="403">Clients are not allowed to list all users</response>
    [HttpGet]
    [ProducesResponseType(typeof(List<UserResponse>), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<ActionResult<List<UserResponse>>> GetAll(CancellationToken ct)
    {
        if (!currentUser.IsEmployee)
            return StatusCode(StatusCodes.Status403Forbidden);

        var result = await userService.GetAllAsync(ct);
        return Ok(result.Value);
    }

    /// <summary>Get a user profile by ID</summary>
    /// <param name="id">User ID</param>
    /// <param name="ct"></param>
    /// <response code="200">User profile returned</response>
    /// <response code="403">Client may only view their own profile</response>
    /// <response code="404">User not found</response>
    [HttpGet("{id:int}")]
    [ProducesResponseType(typeof(UserResponse), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<UserResponse>> GetById([FromRoute] int id, CancellationToken ct)
    {
        if (currentUser.IsClient && currentUser.UserId != id)
            return StatusCode(StatusCodes.Status403Forbidden);

        var result = await userService.GetByIdAsync(id, ct);

        if (result.IsFailed)
            return result.HasError<NotFoundError>() ? NotFound() : BadRequest();

        return Ok(result.Value);
    }

    /// <summary>Create a new client or employee</summary>
    /// <response code="201">User created successfully</response>
    /// <response code="403">Only employees may create users</response>
    /// <response code="409">Username or email is already taken</response>
    [HttpPost]
    [ProducesResponseType(typeof(UserResponse), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<ActionResult<UserResponse>> Create(
        [FromBody] CreateUserRequest request,
        CancellationToken ct
    )
    {
        if (!currentUser.IsEmployee)
            return StatusCode(StatusCodes.Status403Forbidden);

        var result = await userService.CreateAsync(request, ct);

        if (result.IsFailed)
            return result.HasError<ConflictError>() ? Conflict() : BadRequest();

        return CreatedAtAction(nameof(GetById), new { id = result.Value.Id }, result.Value);
    }

    /// <summary>Block a user</summary>
    /// <param name="id">User ID</param>
    /// <param name="ct"></param>
    /// <response code="204">User blocked successfully</response>
    /// <response code="403">Only employees may block users</response>
    /// <response code="404">User not found</response>
    [HttpPatch("{id:int}/block")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> Block([FromRoute] int id, CancellationToken ct)
    {
        if (!currentUser.IsEmployee)
            return StatusCode(StatusCodes.Status403Forbidden);

        var result = await userService.BlockAsync(id, ct);

        if (result.IsFailed)
            return result.HasError<NotFoundError>() ? NotFound() : BadRequest();

        return NoContent();
    }

    /// <summary>Unblock a user</summary>
    /// <param name="id">User ID</param>
    /// <param name="ct"></param>
    /// <response code="204">User unblocked successfully</response>
    /// <response code="403">Only employees may unblock users</response>
    /// <response code="404">User not found</response>
    [HttpPatch("{id:int}/unblock")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> Unblock([FromRoute] int id, CancellationToken ct)
    {
        if (!currentUser.IsEmployee)
            return StatusCode(StatusCodes.Status403Forbidden);

        var result = await userService.UnblockAsync(id, ct);

        if (result.IsFailed)
            return result.HasError<NotFoundError>() ? NotFound() : BadRequest();

        return NoContent();
    }
}
