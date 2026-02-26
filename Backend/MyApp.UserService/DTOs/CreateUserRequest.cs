using System.ComponentModel.DataAnnotations;
using MyApp.UserService.Models;

namespace MyApp.UserService.DTOs;

/// <summary>Request to create a new user (client or employee)</summary>
/// <param name="Username">Unique username (3–64 characters)</param>
/// <param name="Password">Password (minimum 6 characters)</param>
/// <param name="Role">Role: 0 — Client, 1 — Employee</param>
public record CreateUserRequest(
    [Required] [MinLength(3)] [MaxLength(64)] string Username,
    [Required] [MinLength(6)] string Password,
    Role Role
// I guess we don't need to specify IsBlocked, but if we want to create user being blocked, let me know
);

// TODO: add validation attributes for password
