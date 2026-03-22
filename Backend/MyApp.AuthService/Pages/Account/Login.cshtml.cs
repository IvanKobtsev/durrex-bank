using Duende.IdentityServer.Services;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using MyApp.AuthService.Models;

namespace MyApp.AuthService.Pages.Account;

public class LoginModel(
    SignInManager<ApplicationUser> signInManager,
    IIdentityServerInteractionService interaction) : PageModel
{
    [BindProperty(SupportsGet = true)]
    public string? ReturnUrl { get; set; }

    [BindProperty]
    public InputModel Input { get; set; } = new();

    public class InputModel
    {
        public string Email { get; set; } = null!;
        public string Password { get; set; } = null!;
    }

    public async Task<IActionResult> OnPostAsync()
    {
        var context = await interaction.GetAuthorizationContextAsync(ReturnUrl);

        var result = await signInManager.PasswordSignInAsync(
            Input.Email, Input.Password, isPersistent: false, lockoutOnFailure: true);

        if (result.Succeeded)
        {
            if (context is not null)
                return Redirect(ReturnUrl!);

            if (Url.IsLocalUrl(ReturnUrl))
                return Redirect(ReturnUrl!);

            return Redirect("~/");
        }

        ModelState.AddModelError("", "Invalid credentials.");
        return Page();
    }
}
