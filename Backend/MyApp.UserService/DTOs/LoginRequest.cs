using System.ComponentModel.DataAnnotations;

namespace MyApp.UserService.DTOs;

/// <summary>Request to obtain a JWT token</summary>
/// <param name="Username">Username</param>
/// <param name="Password">Password</param>
public record LoginRequest(
    [Required] string Username,
    [Required] string Password
);
