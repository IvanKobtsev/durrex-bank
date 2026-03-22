namespace MyApp.MobileAppSettingsService.DTOs;

public record UserSettingsDto(string Theme, int[] HiddenAccountIds);

public record UpdateThemeDto(string Theme);

public record UpdateHiddenAccountsDto(int[]? Add, int[]? Remove);
