using MyApp.UserService.Data;
using MyApp.UserService.DTOs;
using MyApp.UserService.Infrastructure;
using MyApp.UserService.Models;

namespace MyApp.UserService.Services;

public class UserRegistrationService(
    UserDbContext db,
    AuthServiceClient authServiceClient)
{
    public async Task<(AppUser User, string InviteUrl)> RegisterAsync(
        CreateUserRequest request, CancellationToken ct)
    {
        await using var tx = await db.Database.BeginTransactionAsync(ct);

        var user = new AppUser
        {
            Email           = request.Email,
            Username        = request.Username,
            FirstName       = request.FirstName,
            LastName        = request.LastName,
            TelephoneNumber = request.TelephoneNumber,
            Role            = request.Role,
            IsBlocked       = false
        };
        db.Users.Add(user);
        await db.SaveChangesAsync(ct);

        var inviteUrl = await authServiceClient.CreateInviteAsync(user.Id, user.Email, ct);
        if (inviteUrl is null)
        {
            await tx.RollbackAsync(ct);
            throw new InvalidOperationException("Authentication service is unavailable.");
        }

        await tx.CommitAsync(ct);
        return (user, inviteUrl);
    }
}
