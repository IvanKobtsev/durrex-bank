using Microsoft.AspNetCore.Mvc;
using MyApp.UserService.DTOs;

namespace MyApp.UserService.Controllers;

[ApiController]
[Route("users")]
[Produces("application/json")]
public class UsersController : ControllerBase
{
    /// <summary>Get a list of all users (clients and employees)</summary>
    /// <response code="200">List of users returned</response>
    [HttpGet]
    [ProducesResponseType(typeof(List<UserResponse>), StatusCodes.Status200OK)]
    public async Task<ActionResult<List<UserResponse>>> GetAll()
    {
        throw new NotImplementedException();
    }

    /// <summary>Get a user profile by ID</summary>
    /// <param name="id">User ID</param>
    /// <response code="200">User profile returned</response>
    /// <response code="404">User not found</response>
    [HttpGet("{id:int}")]
    [ProducesResponseType(typeof(UserResponse), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<UserResponse>> GetById([FromRoute] int id)
    {
        throw new NotImplementedException();
    }

    /// <summary>Create a new client or employee</summary>
    /// <response code="201">User created successfully</response>
    /// <response code="409">Username is already taken</response>
    [HttpPost]
    [ProducesResponseType(typeof(UserResponse), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<ActionResult<UserResponse>> Create([FromBody] CreateUserRequest request)
    {
        throw new NotImplementedException();
    }

    /// <summary>Block a user</summary>
    /// <param name="id">User ID</param>
    /// <response code="204">User blocked successfully</response>
    /// <response code="404">User not found</response>
    [HttpPatch("{id:int}/block")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> Block([FromRoute] int id)
    {
        throw new NotImplementedException();
    }

    /// <summary>Unblock a user</summary>
    /// <param name="id">User ID</param>
    /// <response code="204">User unblocked successfully</response>
    /// <response code="404">User not found</response>
    [HttpPatch("{id:int}/unblock")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> Unblock([FromRoute] int id)
    {
        throw new NotImplementedException();
    }
}
