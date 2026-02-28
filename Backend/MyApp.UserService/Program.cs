using System.Reflection;
using System.Text.Json.Serialization;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi;
using Microsoft.OpenApi.Models;
using MyApp.UserService.Data;
using MyApp.UserService.Infrastructure;
using MyApp.UserService.Repositories;
using MyApp.UserService.Services;

var builder = WebApplication.CreateBuilder(args);

builder
    .Services.AddControllers()
    .AddJsonOptions(options =>
        options.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter())
    );

builder.Services.AddSwaggerGen(options =>
{
    options.SwaggerDoc(
        "v1",
        new()
        {
            Title = "UserService API",
            Version = "v1",
            Description = "Manages user profiles, authentication and JWT issuance for Durrex Bank.",
        }
    );

    var xmlFile = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
    var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);
    options.IncludeXmlComments(xmlPath);

    options.UseInlineDefinitionsForEnums();

    // options.AddSecurityDefinition(
    //     "InternalApiKey",
    //     new OpenApiSecurityScheme
    //     {
    //         Type = SecuritySchemeType.ApiKey,
    //         In = ParameterLocation.Header,
    //         Name = "X-Internal-Api-Key",
    //         Description = "Internal API key (from appsettings InternalApiKey)",
    //     }
    // );

    // options.AddSecurityRequirement(
    //     new OpenApiSecurityRequirement
    //     {
    //         { new OpenApiSecuritySchemeReference("InternalApiKey"), new List<string>() },
    //     }
    // );
});

builder.Services.AddDbContext<UserDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection"))
);
builder.Services.AddScoped<DataSeeder>();
builder.Services.AddSingleton<RsaKeyProvider>();
builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddScoped<IUserService, UserService>();
builder.Services.AddScoped<IUserRepository, UserRepository>();

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<UserDbContext>();
    await db.Database.MigrateAsync();

    var seeder = scope.ServiceProvider.GetRequiredService<DataSeeder>();
    await seeder.SeedAsync();
}

app.UseSwagger();
app.UseSwaggerUI(options =>
{
    options.SwaggerEndpoint("/swagger/v1/swagger.json", "UserService v1");
    options.RoutePrefix = "swagger";
});

app.UseMiddleware<InternalApiKeyMiddleware>();

app.MapControllers();

app.Run();
