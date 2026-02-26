using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using FluentResults;
using Microsoft.IdentityModel.Tokens;
using MyApp.UserService.DTOs;
using MyApp.UserService.Infrastructure;
using MyApp.UserService.Repositories;
using MyApp.UserService.Services.Errors;

namespace MyApp.UserService.Services;

public class AuthService(
    IUserRepository userRepository,
    RsaKeyProvider rsaKeyProvider,
    IConfiguration configuration
) : IAuthService
{
    public async Task<Result<LoginResponse>> LoginAsync(
        LoginRequest request,
        CancellationToken ct = default
    )
    {
        var user = await userRepository.FindByUsernameAsync(request.Username, ct);

        if (user is null || !BCrypt.Net.BCrypt.Verify(request.Password, user.PasswordHash))
            return Result.Fail(new UnauthorizedError("Invalid username or password."));

        if (user.IsBlocked)
            return Result.Fail(new UnauthorizedError("User account is blocked."));

        // TODO: We have no refresh token, for dev purposes - 10 hrs.
        var expiresMinutes = configuration.GetValue<int>("Jwt:ExpiresInMinutes", 600);
        var expiresAt = DateTime.UtcNow.AddMinutes(expiresMinutes);

        var credentials = new SigningCredentials(
            rsaKeyProvider.PrivateKey,
            SecurityAlgorithms.RsaSha256
        );

        var token = new JwtSecurityToken(
            issuer: configuration["Jwt:Issuer"],
            audience: configuration["Jwt:Audience"],
            claims:
            [
                new Claim(JwtRegisteredClaimNames.Sub, user.Id.ToString()),
                new Claim("role", user.Role.ToString()),
                new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()),
            ],
            expires: expiresAt,
            signingCredentials: credentials
        );

        var tokenString = new JwtSecurityTokenHandler().WriteToken(token);

        return Result.Ok(new LoginResponse(tokenString, expiresAt));
    }

    public string GetPublicKeyPem() => rsaKeyProvider.PublicKeyPem;
}
