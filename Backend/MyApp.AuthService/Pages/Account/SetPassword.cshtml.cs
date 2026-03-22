using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.EntityFrameworkCore;
using MyApp.AuthService.Data;
using MyApp.AuthService.Models;

namespace MyApp.AuthService.Pages.Account;

public class SetPasswordModel(
    UserManager<ApplicationUser> userManager,
    AuthDbContext db) : PageModel
{
    [BindProperty(SupportsGet = true)]
    public string Token { get; set; } = null!;

    [BindProperty]
    public string Password { get; set; } = null!;

    [BindProperty]
    public string ConfirmPassword { get; set; } = null!;

    public IActionResult OnGet() => Page();

    public async Task<IActionResult> OnPostAsync()
    {
        if (Password != ConfirmPassword)
        {
            ModelState.AddModelError("", "Passwords do not match.");
            return Page();
        }

        var invite = await db.InviteTokens
            .FirstOrDefaultAsync(t => t.Token == Token && !t.IsUsed && t.ExpiresAt > DateTimeOffset.UtcNow);

        if (invite is null)
        {
            ModelState.AddModelError("", "Invalid or expired invite link.");
            return Page();
        }

        var user = await userManager.FindByIdAsync(invite.UserId.ToString());
        if (user is null)
        {
            ModelState.AddModelError("", "User not found.");
            return Page();
        }

        // Set password (replaces any existing hash)
        var token = await userManager.GeneratePasswordResetTokenAsync(user);
        var result = await userManager.ResetPasswordAsync(user, token, Password);
        if (!result.Succeeded)
        {
            foreach (var e in result.Errors)
                ModelState.AddModelError("", e.Description);
            return Page();
        }

        invite.IsUsed = true;
        await db.SaveChangesAsync();

        return RedirectToPage("/Account/Login", new { returnUrl = "/" });
    }
}
