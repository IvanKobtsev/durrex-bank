using Microsoft.AspNetCore.Mvc;
using MyApp.UserService.Auth;
using MyApp.UserService.Repositories;

namespace MyApp.UserService.Controllers;

/// <summary>
/// Internal endpoints — accessible only via X-Internal-Api-Key (InternalApiKeyMiddleware).
/// Not exposed through the Gateway to end users.
/// </summary>
[ApiController]
[Route("internal/users")]
public class InternalUsersController(IUserRepository userRepository) : ControllerBase
{
    [HttpGet("{id}/auth-profile")]
    public async Task<IActionResult> GetAuthProfile(int id, CancellationToken ct)
    {
        var user = await userRepository.FindByIdAsync(id, ct);
        if (user is null)
            return NotFound();

        return Ok(new
        {
            UserId = user.Id,
            ExpandedRoles = RoleHierarchy.Expand(user.Role.ToString()).ToList(),
            IsBlocked = user.IsBlocked
        });
    }
}
