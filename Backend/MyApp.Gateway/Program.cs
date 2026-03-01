using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using System.Security.Cryptography;
using System.Text;
using System.Text.Json;
using System.Text.Json.Nodes;
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
        
        if (builderContext.Route.RouteId == "CoreRoute")
        {
            builderContext.AddResponseTransform(async transformContext =>
            {
                var response = transformContext.ProxyResponse;

                if (response?.Content == null) return;

                var json = await response.Content.ReadAsStringAsync();

                var node = JsonNode.Parse(json);

                if (node is not JsonObject obj)
                {
                    return;
                }

                obj["servers"] = new JsonArray
                {
                    new JsonObject { ["url"] = "/core" }
                };

                var modified = obj.ToJsonString(new JsonSerializerOptions { WriteIndented = false });

                response.Content = new StringContent(
                    modified,
                    Encoding.UTF8,
                    "application/json");
            });
        }
        
        if (builderContext.Route.RouteId == "CreditRoute")
        {
            builderContext.AddResponseTransform(async transformContext =>
            {
                var response = transformContext.ProxyResponse;

                if (response?.Content == null) return;

                var json = await response.Content.ReadAsStringAsync();

                var node = JsonNode.Parse(json);

                if (node is not JsonObject obj)
                {
                    return;
                }

                obj["servers"] = new JsonArray
                {
                    new JsonObject { ["url"] = "/credit" }
                };

                var modified = obj.ToJsonString(new JsonSerializerOptions { WriteIndented = false });

                response.Content = new StringContent(
                    modified,
                    Encoding.UTF8,
                    "application/json");
            });
        }
        
        if (builderContext.Route.RouteId == "UserRoute")
        {
            builderContext.AddResponseTransform(async transformContext =>
            {
                var response = transformContext.ProxyResponse;

                if (response?.Content == null) return;

                var json = await response.Content.ReadAsStringAsync();

                var node = JsonNode.Parse(json);

                if (node is not JsonObject obj)
                {
                    return;
                }

                obj["servers"] = new JsonArray
                {
                    new JsonObject { ["url"] = "/user" }
                };

                var modified = obj.ToJsonString(new JsonSerializerOptions { WriteIndented = false });

                response.Content = new StringContent(
                    modified,
                    Encoding.UTF8,
                    "application/json");
            });
        }
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

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        var key = builder.Services
            .BuildServiceProvider()
            .GetRequiredService<RsaSecurityKey>();

        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = key,
            ValidateIssuer = false,
            ValidateAudience = false,
            ValidateLifetime = true
        };
    });

builder.Services.AddAuthorization();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

app.UseAuthentication();
app.UseAuthorization();

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

app.MapReverseProxy();

Console.WriteLine($"Environment: {builder.Environment.EnvironmentName}");
app.Run();