using FluentResults;

namespace MyApp.UserService.Services.Errors;

public class NotFoundError(string message) : Error(message);
