using FluentResults;
using MyApp.UserService.DTOs;

namespace MyApp.UserService.Services;

public interface IUserService
{
    Task<Result<UserResponse>> GetByIdAsync(int id, CancellationToken ct = default);
    Task<Result<List<UserResponse>>> GetAllAsync(CancellationToken ct = default);
    Task<Result<UserResponse>> CreateAsync(CreateUserRequest request, CancellationToken ct = default);
    Task<Result> BlockAsync(int id, CancellationToken ct = default);
    Task<Result> UnblockAsync(int id, CancellationToken ct = default);
}
