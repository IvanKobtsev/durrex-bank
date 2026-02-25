using Microsoft.EntityFrameworkCore;
using MyApp.CoreService.Data;

namespace MyApp.CoreService.Tests.Unit.Helpers;

internal static class DbContextFactory
{
    /// <summary>Creates a fresh, isolated in-memory CoreDbContext for each test.</summary>
    internal static CoreDbContext Create()
    {
        var options = new DbContextOptionsBuilder<CoreDbContext>()
            .UseInMemoryDatabase(Guid.NewGuid().ToString())
            .Options;

        return new CoreDbContext(options);
    }
}
