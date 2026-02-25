namespace MyApp.UserService.DTOs;

/// <summary>Public RSA key for JWT validation on the Gateway side</summary>
/// <param name="PublicKeyPem">Public key in PEM format</param>
public record PublicKeyResponse(string PublicKeyPem);
