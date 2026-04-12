using MediatR;
using Microsoft.EntityFrameworkCore;
using MyApp.CoreService.Auth;
using MyApp.CoreService.Data;
using MyApp.CoreService.Models;

namespace MyApp.CoreService.Features.Notifications.Commands;

public class SubscribeHandler(CoreDbContext db, ICurrentUserContext userContext)
    : IRequestHandler<Subscribe>
{
    public async Task Handle(Subscribe request, CancellationToken ct)
    {
        if (userContext.UserId is null)
            throw new UnauthorizedAccessException(
                "User ID is required to subscribe for notifications."
            );

        // Upsert the Firebase token for this user
        var existingToken = await db.FirebaseTokens.FindAsync(
            new object[] { request.FireBaseToken },
            cancellationToken: ct
        );

        if (existingToken != null)
        {
            // Update existing token with user and role info
            existingToken.UserId = userContext.UserId.Value;
            existingToken.Role = userContext.Role;
        }
        else
        {
            // Create new token entry
            var token = new FirebaseToken
            {
                Token = request.FireBaseToken,
                UserId = userContext.UserId.Value,
                Role = userContext.Role,
            };
            db.FirebaseTokens.Add(token);
        }

        await db.SaveChangesAsync(ct);
    }
}
