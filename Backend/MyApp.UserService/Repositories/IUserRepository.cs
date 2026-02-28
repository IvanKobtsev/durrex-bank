using MyApp.UserService.Models;

namespace MyApp.UserService.Repositories;

public interface IUserRepository
{
    Task<AppUser?> FindByIdAsync(int id, CancellationToken ct = default);
    Task<AppUser?> FindByEmailAsync(string email, CancellationToken ct = default);
    Task<AppUser?> FindByUsernameAsync(string username, CancellationToken ct = default);
    Task<List<AppUser>> GetAllAsync(CancellationToken ct = default);
    Task AddAsync(AppUser user, CancellationToken ct = default);
    Task UpdateAsync(AppUser user, CancellationToken ct = default);
}
