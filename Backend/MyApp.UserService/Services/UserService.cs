using FluentResults;
using MyApp.UserService.DTOs;
using MyApp.UserService.Models;
using MyApp.UserService.Repositories;
using MyApp.UserService.Services.Errors;

namespace MyApp.UserService.Services;

public class UserService(IUserRepository userRepository) : IUserService
{
    public async Task<Result<UserResponse>> GetByIdAsync(int id, CancellationToken ct = default)
    {
        var user = await userRepository.FindByIdAsync(id, ct);
        if (user is null)
            return Result.Fail(new NotFoundError($"User {id} not found."));

        return Result.Ok(MapToResponse(user));
    }

    public async Task<Result<List<UserResponse>>> GetAllAsync(CancellationToken ct = default)
    {
        var users = await userRepository.GetAllAsync(ct);
        return Result.Ok(users.Select(MapToResponse).ToList());
    }

    public async Task<Result<UserResponse>> CreateAsync(
        CreateUserRequest request,
        CancellationToken ct = default
    )
    {
        var existing = await userRepository.FindByUsernameAsync(request.Username, ct);
        if (existing is not null)
            return Result.Fail(
                new ConflictError($"Username '{request.Username}' is already taken.")
            );

        var user = new AppUser
        {
            Username = request.Username,
            PasswordHash = BCrypt.Net.BCrypt.HashPassword(request.Password),
            Role = request.Role,
            IsBlocked = request.IsBlocked,
        };

        await userRepository.AddAsync(user, ct);
        return Result.Ok(MapToResponse(user));
    }

    public async Task<Result> BlockAsync(int id, CancellationToken ct = default)
    {
        var user = await userRepository.FindByIdAsync(id, ct);
        if (user is null)
            return Result.Fail(new NotFoundError($"User {id} not found."));

        user.IsBlocked = true;
        await userRepository.UpdateAsync(user, ct);
        return Result.Ok();
    }

    public async Task<Result> UnblockAsync(int id, CancellationToken ct = default)
    {
        var user = await userRepository.FindByIdAsync(id, ct);
        if (user is null)
            return Result.Fail(new NotFoundError($"User {id} not found."));

        user.IsBlocked = false;
        await userRepository.UpdateAsync(user, ct);
        return Result.Ok();
    }

    private static UserResponse MapToResponse(AppUser user) =>
        new(user.Id, user.Username, user.Role, user.IsBlocked);
}
