using Microsoft.AspNetCore.SignalR;
using MyApp.CoreService.Auth;
using MyApp.CoreService.Data;

namespace MyApp.CoreService.Hubs;

public class TransactionHub(CoreDbContext db, ICurrentUserContext user) : Hub
{
    public async Task SubscribeToAccount(int accountId)
    {
        if (user.IsClient)
        {
            var account = await db.Accounts.FindAsync([accountId], Context.ConnectionAborted);
            if (account is null || account.OwnerId != user.UserId)
            {
                await Clients.Caller.SendAsync("Error", "Access denied.", Context.ConnectionAborted);
                return;
            }
        }

        await Groups.AddToGroupAsync(Context.ConnectionId, $"account-{accountId}", Context.ConnectionAborted);
        await Clients.Caller.SendAsync("Subscribed", accountId, Context.ConnectionAborted);
    }
}
