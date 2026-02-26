namespace MyApp.UserService.DTOs;

/// <summary>JWT token issued after successful login</summary>
/// <param name="Token">Bearer token for the Authorization header</param>
/// <param name="ExpiresAt">Token expiration time (UTC)</param>
public record LoginResponse(string Token, DateTime ExpiresAt);
