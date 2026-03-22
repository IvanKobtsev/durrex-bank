using System.Security.Cryptography;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using MyApp.AuthService.Data;
using MyApp.AuthService.Models;

namespace MyApp.AuthService.Controllers;

[ApiController]
[Route("internal/invite")]
public class InviteController(
    UserManager<ApplicationUser> userManager,
    AuthDbContext db,
    IConfiguration config) : ControllerBase
{
    [HttpPost]
    public async Task<IActionResult> CreateInvite([FromBody] CreateInviteRequest request)
    {
        await using var tx = await db.Database.BeginTransactionAsync();

        var existing = await userManager.FindByIdAsync(request.UserId.ToString());
        if (existing is null)
        {
            var user = new ApplicationUser
            {
                Id = request.UserId,
                UserName = request.Email,
                Email = request.Email,
                EmailConfirmed = true
            };
            var result = await userManager.CreateAsync(user);
            if (!result.Succeeded)
            {
                await tx.RollbackAsync();
                return BadRequest(result.Errors);
            }
        }

        var token = Convert.ToHexString(RandomNumberGenerator.GetBytes(32));
        db.InviteTokens.Add(new InviteToken
        {
            Token = token,
            UserId = request.UserId,
            ExpiresAt = DateTimeOffset.UtcNow.AddDays(7),
            IsUsed = false
        });
        await db.SaveChangesAsync();

        await tx.CommitAsync();

        var baseUrl = config["IdentityServer:IssuerUri"];
        return Ok(new { InviteUrl = $"{baseUrl}/account/set-password?token={token}" });
    }
}

public record CreateInviteRequest(int UserId, string Email);
