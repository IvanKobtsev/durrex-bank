using System.Reflection;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi.Models;
using MyApp.CreditService.Middleware;
using MyApp.CreditService.Services;
using MyApp.CreditService.Swagger;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();

builder.Services.AddHttpClient<ICoreServiceClient, CoreServiceClient>(c =>
    c.BaseAddress = new Uri(builder.Configuration["Services:CoreService:BaseUrl"]!)
);

builder.Services.AddMediatR(cfg => cfg.RegisterServicesFromAssembly(typeof(Program).Assembly));

builder.Services.AddDbContext<CreditDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("Default"))
);

builder.Services.AddHostedService<PaymentSchedulerService>();

builder.Services.AddSwaggerGen(options =>
{
    options.SwaggerDoc(
        "v1",
        new OpenApiInfo
        {
            Title = "CreditService API",
            Version = "v1",
            Description = "Manages credit tariffs, loan issuance and repayment scheduling for Durrex Bank.",
        }
    );

    var xmlFile = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
    var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);
    options.IncludeXmlComments(xmlPath);

    options.OperationFilter<GatewayHeadersOperationFilter>();

    options.AddSecurityDefinition(
        "InternalApiKey",
        new OpenApiSecurityScheme
        {
            Type = SecuritySchemeType.ApiKey,
            In = ParameterLocation.Header,
            Name = "X-Internal-Api-Key",
            Description = "Internal API key required for all endpoints (from appsettings InternalApiKey).",
        }
    );

    options.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference { Type = ReferenceType.SecurityScheme, Id = "InternalApiKey" }
            },
            []
        }
    });
});

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<CreditDbContext>();
    await db.Database.MigrateAsync();
}

app.UseSwagger();
app.UseSwaggerUI(options =>
{
    options.SwaggerEndpoint("/swagger/v1/swagger.json", "CreditService v1");
    options.RoutePrefix = "swagger";
});

app.UseMiddleware<InternalApiKeyMiddleware>();

app.MapControllers();

app.Run();
