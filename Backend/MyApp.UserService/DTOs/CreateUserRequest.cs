using System.ComponentModel.DataAnnotations;
using MccSoft.WebApi.Domain.Helpers;
using MyApp.UserService.Models;

namespace MyApp.UserService.DTOs;

/// <summary>Request to create a new user (client or employee)</summary>
/// <param name="Username">Unique username (3–64 characters)</param>
/// <param name="Password">Password (minimum 6 characters)</param>
/// <param name="Role">Role: 0 — Client, 1 — Employee</param>
/// <param name="Role">Blocked or not</param>
public record CreateUserRequest(
    [Required] [MinLength(3)] [MaxLength(64)] string Username,
    [Required] [MinLength(6)] [AllowedPasswordChars] string Password,
    Role Role,
    bool IsBlocked = false
);
