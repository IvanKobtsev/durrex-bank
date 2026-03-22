namespace MyApp.WebAppSettingsService.Models;

public class UserSettings
{
    public int UserId { get; set; }
    public string Theme { get; set; } = "light";
    public DateTimeOffset UpdatedAt { get; set; }
}
