using FluentResults;
using MyApp.UserService.DTOs;

namespace MyApp.UserService.Services;

public interface IAuthService
{
    Task<Result<LoginResponse>> LoginAsync(LoginRequest request, CancellationToken ct = default);
    string GetPublicKeyPem();
}
