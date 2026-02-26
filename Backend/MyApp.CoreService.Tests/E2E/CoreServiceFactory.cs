using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.AspNetCore.TestHost;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using MyApp.CoreService.Data;

namespace MyApp.CoreService.Tests.E2E;

/// <summary>
/// Spins up the full CoreService pipeline against an isolated in-memory database.
/// ConfigureTestServices runs AFTER Program.cs service registration so we can safely
/// remove the Npgsql DbContext and replace it with the in-memory provider.
/// One factory instance = one isolated database, so tests within a class share state.
/// </summary>
public class CoreServiceFactory : WebApplicationFactory<Program>
{
    public const string TestApiKey = "e2e-test-api-key";

    private readonly string _databaseName = Guid.NewGuid().ToString();

    protected override void ConfigureWebHost(IWebHostBuilder builder)
    {
        // "Testing" environment causes Program.cs to skip MigrateAsync
        builder.UseEnvironment("Testing");
        // Inject a known API key so tests can authenticate
        builder.UseSetting("InternalApiKey", TestApiKey);

        // ConfigureTestServices runs AFTER the app's service registration.
        // We remove all DbContext-related descriptors and register fresh InMemory ones.
        builder.ConfigureTestServices(services =>
        {
            // Remove every descriptor that references CoreDbContext or its options
            var toRemove = services
                .Where(d =>
                    d.ServiceType == typeof(DbContextOptions<CoreDbContext>) ||
                    d.ServiceType == typeof(DbContextOptions) ||
                    d.ServiceType == typeof(CoreDbContext))
                .ToList();
            foreach (var d in toRemove)
                services.Remove(d);

            // Build options directly so only one provider (InMemory) is ever present
            var options = new DbContextOptionsBuilder<CoreDbContext>()
                .UseInMemoryDatabase(_databaseName)
                .Options;

            services.AddSingleton(options);
            services.AddScoped<CoreDbContext>();
        });
    }

    /// <summary>Creates an HttpClient pre-configured with the internal API key header.</summary>
    public HttpClient CreateAuthenticatedClient()
    {
        var client = CreateClient();
        client.DefaultRequestHeaders.Add("X-Internal-Api-Key", TestApiKey);
        return client;
    }

    /// <summary>Creates an HttpClient acting as a Client user with the given userId.</summary>
    public HttpClient CreateClientUserClient(int userId)
    {
        var client = CreateAuthenticatedClient();
        client.DefaultRequestHeaders.Add("X-User-Id", userId.ToString());
        client.DefaultRequestHeaders.Add("X-User-Role", "Client");
        return client;
    }

    /// <summary>Creates an HttpClient acting as an Employee user with the given userId.</summary>
    public HttpClient CreateEmployeeClient(int userId)
    {
        var client = CreateAuthenticatedClient();
        client.DefaultRequestHeaders.Add("X-User-Id", userId.ToString());
        client.DefaultRequestHeaders.Add("X-User-Role", "Employee");
        return client;
    }
}
