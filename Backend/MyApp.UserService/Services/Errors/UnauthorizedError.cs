using FluentResults;

namespace MyApp.UserService.Services.Errors;

public class UnauthorizedError(string message) : Error(message);
