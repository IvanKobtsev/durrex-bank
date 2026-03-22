namespace MyApp.WebAppSettingsService.DTOs;

public record UserSettingsDto(string Theme);

public record UpdateThemeDto(string Theme);

public record UpdateHiddenAccountsDto(int[]? Add, int[]? Remove);
