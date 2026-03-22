using Duende.IdentityServer.Models;
using Duende.IdentityServer.Services;
using Microsoft.AspNetCore.Identity;
using MyApp.AuthService.Infrastructure;
using MyApp.AuthService.Models;
using System.Security.Claims;

namespace MyApp.AuthService.Services;

public class HierarchicalProfileService(
    UserManager<ApplicationUser> userManager,
    UserServiceClient userServiceClient) : IProfileService
{
    public async Task GetProfileDataAsync(ProfileDataRequestContext context)
    {
        var user = await userManager.GetUserAsync(context.Subject);
        if (user is null) return;

        var profile = await userServiceClient.GetAuthProfileAsync(user.Id);
        if (profile is null) return;

        context.IssuedClaims.AddRange(
            profile.ExpandedRoles.Select(r => new Claim("role", r))
        );
    }

    public async Task IsActiveAsync(IsActiveContext context)
    {
        var user = await userManager.GetUserAsync(context.Subject);
        if (user is null)
        {
            context.IsActive = false;
            return;
        }

        var profile = await userServiceClient.GetAuthProfileAsync(user.Id);
        context.IsActive = profile is not null && !profile.IsBlocked;
    }
}
