using Microsoft.IdentityModel.Tokens;
using System.Security.Cryptography;
using System.Text.Json;
using MyApp.Gateway;
using Yarp.ReverseProxy.Transforms;

var builder = WebApplication.CreateBuilder(args);

builder.Logging.AddConsole();
builder.Services.AddReverseProxy()
    .LoadFromConfig(builder.Configuration.GetSection("ReverseProxy")).AddTransforms(builderContext =>
    {
        builderContext.AddRequestTransform(transformContext =>
        {
            transformContext.ProxyRequest.Headers.Add(
                "X-Internal-Api-Key",
                builder.Configuration["InternalApiKey"]
            );

            return ValueTask.CompletedTask;
        });
        
        SwaggerResponseTransformUtil.AddTransformIfMatch(builderContext);
    });

builder.Services.AddHttpClient("AuthClient", client =>
{
    client.BaseAddress = new Uri("http://localhost:5004/");
    client.DefaultRequestHeaders.Add("X-Internal-Api-Key",
        builder.Configuration["InternalApiKey"]);
});

builder.Services.AddSingleton<RsaSecurityKey>(provider =>
{
    var client = provider
        .GetRequiredService<IHttpClientFactory>()
        .CreateClient("AuthClient");

    var response = client.GetStringAsync("auth/public-key").Result;

    var publicKey = JsonSerializer.Deserialize<PublicKeyResponse>(response)!;

    var rsa = RSA.Create();
    rsa.ImportFromPem(publicKey.publicKeyPem);

    return new RsaSecurityKey(rsa);
});


builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddCors(options =>
{
    options.AddPolicy("DevCors", policy =>
    {
        policy
            .WithOrigins("http://localhost:5173")
            .AllowAnyHeader()
            .AllowAnyMethod()
            .AllowCredentials();
    });
});

var app = builder.Build();

app.UseSwagger();

app.UseSwaggerUI(options =>
{
    options.SwaggerEndpoint(
        "/core/openapi/v1.json",
        "CoreService API");
    
    options.SwaggerEndpoint(
        "/credit/swagger/v1/swagger.json",
        "CreditService API");

    options.SwaggerEndpoint(
        "/user/swagger/v1/swagger.json",
        "UserService API");

    options.RoutePrefix = "swagger";
});

app.UseCors("DevCors");
app.UseMiddleware<JwtForwardingMiddleware>();
app.MapReverseProxy();

Console.WriteLine($"Environment: {builder.Environment.EnvironmentName}");
app.Run();