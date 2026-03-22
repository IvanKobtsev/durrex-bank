using Microsoft.AspNetCore.Mvc;
using MyApp.WebAppSettingsService.Auth;
using MyApp.WebAppSettingsService.Data;
using MyApp.WebAppSettingsService.DTOs;
using MyApp.WebAppSettingsService.Models;

namespace MyApp.WebAppSettingsService.Controllers;

[ApiController]
[Route("api/settings")]
public class SettingsController(SettingsDbContext db, ICurrentUserContext user) : ControllerBase
{
    [HttpGet]
    public async Task<ActionResult<UserSettingsDto>> Get(CancellationToken ct)
    {
        if (user.UserId is null)
            return Unauthorized();
        var settings = await GetOrCreateAsync(user.UserId.Value, ct);
        return Ok(new UserSettingsDto(settings.Theme));
    }

    [HttpGet("{userId:int}")]
    public async Task<ActionResult<UserSettingsDto>> GetForUser(int userId, CancellationToken ct)
    {
        if (user.IsClient)
            return StatusCode(StatusCodes.Status403Forbidden);
        var settings = await GetOrCreateAsync(userId, ct);
        return Ok(new UserSettingsDto(settings.Theme));
    }

    [HttpPut]
    public async Task<ActionResult<UserSettingsDto>> Put(
        [FromBody] UserSettingsDto dto,
        CancellationToken ct
    )
    {
        if (user.UserId is null)
            return Unauthorized();
        var settings = await GetOrCreateAsync(user.UserId.Value, ct);
        settings.Theme = dto.Theme;
        settings.UpdatedAt = DateTimeOffset.UtcNow;
        await db.SaveChangesAsync(ct);
        return Ok(new UserSettingsDto(settings.Theme));
    }

    [HttpPatch("theme")]
    public async Task<IActionResult> PatchTheme([FromBody] UpdateThemeDto dto, CancellationToken ct)
    {
        if (user.UserId is null)
            return Unauthorized();
        if (dto.Theme is not ("light" or "dark"))
            return BadRequest(new { error = "Theme must be 'light' or 'dark'." });
        var settings = await GetOrCreateAsync(user.UserId.Value, ct);
        settings.Theme = dto.Theme;
        settings.UpdatedAt = DateTimeOffset.UtcNow;
        await db.SaveChangesAsync(ct);
        return Ok(new UserSettingsDto(settings.Theme));
    }

    private async Task<UserSettings> GetOrCreateAsync(int userId, CancellationToken ct)
    {
        var s = await db.Settings.FindAsync([userId], ct);
        if (s is not null)
            return s;
        s = new UserSettings { UserId = userId, UpdatedAt = DateTimeOffset.UtcNow };
        db.Settings.Add(s);
        return s;
    }
}
