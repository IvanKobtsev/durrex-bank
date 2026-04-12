using Duende.IdentityServer.Services;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using MyApp.AuthService.Models;

namespace MyApp.AuthService.Pages.Account;

public class LogoutModel(
    SignInManager<ApplicationUser> signInManager,
    IIdentityServerInteractionService interaction
) : PageModel
{
    [BindProperty(SupportsGet = true)]
    public string? LogoutId { get; set; }

    public async Task<IActionResult> OnGetAsync()
    {
        var context = await interaction.GetLogoutContextAsync(LogoutId);

        await signInManager.SignOutAsync();
        await HttpContext.SignOutAsync();

        var postLogoutUri = context.PostLogoutRedirectUri;

        if (!string.IsNullOrEmpty(postLogoutUri))
            return Redirect(postLogoutUri);

        return Redirect("~/");
    }

    public async Task<IActionResult> OnPostAsync()
    {
        var context = await interaction.GetLogoutContextAsync(LogoutId);

        await signInManager.SignOutAsync();
        await HttpContext.SignOutAsync();

        var postLogoutUri = context.PostLogoutRedirectUri;

        if (!string.IsNullOrEmpty(postLogoutUri))
            return Redirect(postLogoutUri);

        return Redirect("~/");
    }
}
