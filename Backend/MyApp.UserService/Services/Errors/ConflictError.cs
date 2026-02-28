using FluentResults;

namespace MyApp.UserService.Services.Errors;

public class ConflictError(string message) : Error(message);
