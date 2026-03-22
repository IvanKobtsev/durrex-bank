using Microsoft.AspNetCore.Mvc;
using MyApp.SettingsService.Auth;
using MyApp.SettingsService.Data;
using MyApp.SettingsService.DTOs;
using MyApp.SettingsService.Models;

namespace MyApp.SettingsService.Controllers;

[ApiController]
[Route("api/settings")]
public class SettingsController(SettingsDbContext db, ICurrentUserContext user) : ControllerBase
{
    [HttpGet]
    public async Task<IActionResult> Get(CancellationToken ct)
    {
        if (user.UserId is null)
            return Unauthorized();
        var settings = await GetOrCreateAsync(user.UserId.Value, ct);
        return Ok(new UserSettingsDto(settings.Theme, settings.HiddenAccountIds));
    }

    [HttpGet("{userId:int}")]
    public async Task<IActionResult> GetForUser(int userId, CancellationToken ct)
    {
        if (user.IsClient)
            return StatusCode(StatusCodes.Status403Forbidden);
        var settings = await GetOrCreateAsync(userId, ct);
        return Ok(new UserSettingsDto(settings.Theme, settings.HiddenAccountIds));
    }

    [HttpPut]
    public async Task<IActionResult> Put([FromBody] UserSettingsDto dto, CancellationToken ct)
    {
        if (user.UserId is null)
            return Unauthorized();
        var settings = await GetOrCreateAsync(user.UserId.Value, ct);
        settings.Theme = dto.Theme;
        settings.HiddenAccountIds = dto.HiddenAccountIds;
        settings.UpdatedAt = DateTimeOffset.UtcNow;
        await db.SaveChangesAsync(ct);
        return Ok(new UserSettingsDto(settings.Theme, settings.HiddenAccountIds));
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
        return Ok(new UserSettingsDto(settings.Theme, settings.HiddenAccountIds));
    }

    [HttpPatch("hidden-accounts")]
    public async Task<IActionResult> PatchHiddenAccounts([FromBody] UpdateHiddenAccountsDto dto, CancellationToken ct)
    {
        if (user.UserId is null)
            return Unauthorized();
        var settings = await GetOrCreateAsync(user.UserId.Value, ct);
        var current = settings.HiddenAccountIds.ToHashSet();
        foreach (var id in dto.Add ?? [])
            current.Add(id);
        foreach (var id in dto.Remove ?? [])
            current.Remove(id);
        settings.HiddenAccountIds = current.ToArray();
        settings.UpdatedAt = DateTimeOffset.UtcNow;
        await db.SaveChangesAsync(ct);
        return Ok(new UserSettingsDto(settings.Theme, settings.HiddenAccountIds));
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
