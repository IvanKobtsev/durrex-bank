using MyApp.UserService.Models;

namespace MyApp.UserService.DTOs;

/// <summary>User profile</summary>
/// <param name="Id">Unique identifier</param>
/// <param name="Username">Username</param>
/// <param name="FirstName">First name</param>
/// <param name="LastName">Last name</param>
/// <param name="Email">Email address</param>
/// <param name="TelephoneNumber">Telephone number</param>
/// <param name="Role">Role: 0 — Client, 1 — Employee</param>
/// <param name="IsBlocked">Whether the user is blocked</param>
public record UserResponse(
    int Id,
    string Username,
    string FirstName,
    string LastName,
    string Email,
    string TelephoneNumber,
    Role Role,
    bool IsBlocked
);
