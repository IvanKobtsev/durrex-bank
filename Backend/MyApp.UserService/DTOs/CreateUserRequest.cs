using System.ComponentModel.DataAnnotations;
using MccSoft.WebApi.Domain.Helpers;
using MyApp.UserService.Models;

namespace MyApp.UserService.DTOs;

/// <summary>Request to create a new user (client or employee)</summary>
/// <param name="Email">Unique email address</param>
/// <param name="Username">Unique username (3–64 characters)</param>
/// <param name="Password">Password (minimum 6 characters)</param>
/// <param name="FirstName">First name</param>
/// <param name="LastName">Last name</param>
/// <param name="TelephoneNumber">Unique telephone number</param>
/// <param name="Role">Role: Client, Employee</param>
/// <param name="IsBlocked">Blocked or not</param>
public record CreateUserRequest(
    [Required] [EmailAddress] string Email,
    [Required] [MinLength(3)] [MaxLength(64)] string Username,
    [Required] [MinLength(6)] [AllowedPasswordChars] string Password,
    [Required] [MinLength(1)] [MaxLength(128)] string FirstName,
    [Required] [MinLength(1)] [MaxLength(128)] string LastName,
    [Required] [Phone] [MaxLength(32)] string TelephoneNumber,
    Role Role,
    bool IsBlocked = false
);
