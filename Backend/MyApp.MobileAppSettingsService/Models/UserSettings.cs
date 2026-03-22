namespace MyApp.MobileAppSettingsService.Models;

public class UserSettings
{
    public int UserId { get; set; }
    public string Theme { get; set; } = "light";
    public int[] HiddenAccountIds { get; set; } = [];
    public DateTimeOffset UpdatedAt { get; set; }
}
