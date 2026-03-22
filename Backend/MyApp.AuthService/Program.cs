using Duende.IdentityServer.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using MyApp.AuthService.Data;
using MyApp.AuthService.Infrastructure;
using MyApp.AuthService.Middleware;
using MyApp.AuthService.Models;
using MyApp.AuthService.Services;

var builder = WebApplication.CreateBuilder(args);
var connectionString = builder.Configuration.GetConnectionString("DefaultConnection")!;

builder.Services.AddRazorPages();
builder.Services.AddControllers();

builder.Services.AddDbContext<AuthDbContext>(options =>
    options.UseNpgsql(connectionString));

builder.Services.AddIdentity<ApplicationUser, IdentityRole<int>>(options =>
{
    options.Password.RequireDigit = false;
    options.Password.RequiredLength = 6;
    options.Password.RequireNonAlphanumeric = false;
    options.Password.RequireUppercase = false;
})
.AddEntityFrameworkStores<AuthDbContext>()
.AddDefaultTokenProviders();

builder.Services.AddHttpClient<UserServiceClient>(client =>
{
    client.BaseAddress = new Uri(builder.Configuration["Services:UserService"]!);
    client.DefaultRequestHeaders.Add(
        "X-Internal-Api-Key",
        builder.Configuration["InternalApiKey"]!);
});

var identityResources = builder.Configuration
    .GetSection("IdentityServer:IdentityResources")
    .Get<List<IdentityResource>>() ?? [];

var apiScopes = builder.Configuration
    .GetSection("IdentityServer:ApiScopes")
    .Get<List<ApiScope>>() ?? [];

var apiResources = builder.Configuration
    .GetSection("IdentityServer:ApiResources")
    .Get<List<ApiResource>>() ?? [];

var clients = builder.Configuration
    .GetSection("IdentityServer:Clients")
    .Get<List<Client>>() ?? [];

builder.Services
    .AddIdentityServer(options =>
    {
        options.IssuerUri = builder.Configuration["IdentityServer:IssuerUri"];
        options.Events.RaiseErrorEvents = true;
        options.Events.RaiseFailureEvents = true;
        options.Events.RaiseSuccessEvents = true;
    })
    .AddInMemoryIdentityResources(identityResources)
    .AddInMemoryApiScopes(apiScopes)
    .AddInMemoryApiResources(apiResources)
    .AddInMemoryClients(clients)
    .AddAspNetIdentity<ApplicationUser>()
    .AddProfileService<HierarchicalProfileService>()
    .AddDeveloperSigningCredential();

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AuthDbContext>();
    await db.Database.MigrateAsync();

    var userManager = scope.ServiceProvider.GetRequiredService<UserManager<ApplicationUser>>();
    var cfg = scope.ServiceProvider.GetRequiredService<IConfiguration>();
    await AuthSeeder.SeedAsync(userManager, cfg);
}

app.UseMiddleware<InternalApiKeyMiddleware>();
app.UseStaticFiles();
app.UseRouting();
app.UseIdentityServer();
app.UseAuthorization();
app.MapRazorPages();
app.MapControllers();

app.Run();
