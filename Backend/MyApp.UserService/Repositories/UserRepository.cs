using Microsoft.EntityFrameworkCore;
using MyApp.UserService.Data;
using MyApp.UserService.Models;

namespace MyApp.UserService.Repositories;

public class UserRepository(UserDbContext db) : IUserRepository
{
    public Task<AppUser?> FindByIdAsync(int id, CancellationToken ct = default) =>
        db.Users.FirstOrDefaultAsync(u => u.Id == id, ct);

    public Task<AppUser?> FindByEmailAsync(string email, CancellationToken ct = default) =>
        db.Users.FirstOrDefaultAsync(u => u.Email == email, ct);

    public Task<AppUser?> FindByUsernameAsync(string username, CancellationToken ct = default) =>
        db.Users.FirstOrDefaultAsync(u => u.Username == username, ct);

    public Task<AppUser?> FindByTelephoneNumberAsync(string telephoneNumber, CancellationToken ct = default) =>
        db.Users.FirstOrDefaultAsync(u => u.TelephoneNumber == telephoneNumber, ct);

    public Task<List<AppUser>> GetAllAsync(CancellationToken ct = default) =>
        db.Users.ToListAsync(ct);

    public async Task AddAsync(AppUser user, CancellationToken ct = default)
    {
        db.Users.Add(user);
        await db.SaveChangesAsync(ct);
    }

    public async Task UpdateAsync(AppUser user, CancellationToken ct = default)
    {
        db.Users.Update(user);
        await db.SaveChangesAsync(ct);
    }
}
