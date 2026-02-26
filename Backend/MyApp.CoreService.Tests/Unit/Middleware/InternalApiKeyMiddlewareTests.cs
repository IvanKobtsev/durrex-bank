using FluentAssertions;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Configuration;
using MyApp.CoreService.Middleware;

namespace MyApp.CoreService.Tests.Unit.Middleware;

public class InternalApiKeyMiddlewareTests
{
    private const string ValidKey = "test-api-key-123";
    private const string HeaderName = "X-Internal-Api-Key";

    private static InternalApiKeyMiddleware CreateMiddleware(RequestDelegate next)
    {
        var config = new ConfigurationBuilder()
            .AddInMemoryCollection(new Dictionary<string, string?> { ["InternalApiKey"] = ValidKey })
            .Build();

        return new InternalApiKeyMiddleware(next, config);
    }

    private static DefaultHttpContext CreateContext(string path, string? apiKey = null)
    {
        var ctx = new DefaultHttpContext();
        ctx.Request.Path = path;
        ctx.Response.Body = new MemoryStream();

        if (apiKey is not null)
            ctx.Request.Headers[HeaderName] = apiKey;

        return ctx;
    }

    [Fact]
    public async Task InvokeAsync_ValidApiKey_CallsNextMiddleware()
    {
        var nextCalled = false;
        var middleware = CreateMiddleware(_ => { nextCalled = true; return Task.CompletedTask; });
        var ctx = CreateContext("/api/accounts", apiKey: ValidKey);

        await middleware.InvokeAsync(ctx);

        nextCalled.Should().BeTrue();
        ctx.Response.StatusCode.Should().Be(200);
    }

    [Fact]
    public async Task InvokeAsync_MissingApiKey_Returns401()
    {
        var middleware = CreateMiddleware(_ => Task.CompletedTask);
        var ctx = CreateContext("/api/accounts"); // no key

        await middleware.InvokeAsync(ctx);

        ctx.Response.StatusCode.Should().Be(401);
    }

    [Fact]
    public async Task InvokeAsync_WrongApiKey_Returns401()
    {
        var middleware = CreateMiddleware(_ => Task.CompletedTask);
        var ctx = CreateContext("/api/accounts", apiKey: "wrong-key");

        await middleware.InvokeAsync(ctx);

        ctx.Response.StatusCode.Should().Be(401);
    }

    [Theory]
    [InlineData("/scalar")]
    [InlineData("/scalar/v1")]
    public async Task InvokeAsync_ScalarPath_BypassesKeyCheck(string path)
    {
        var nextCalled = false;
        var middleware = CreateMiddleware(_ => { nextCalled = true; return Task.CompletedTask; });
        var ctx = CreateContext(path); // no key provided

        await middleware.InvokeAsync(ctx);

        nextCalled.Should().BeTrue();
    }

    [Theory]
    [InlineData("/openapi")]
    [InlineData("/openapi/v1.json")]
    public async Task InvokeAsync_OpenApiPath_BypassesKeyCheck(string path)
    {
        var nextCalled = false;
        var middleware = CreateMiddleware(_ => { nextCalled = true; return Task.CompletedTask; });
        var ctx = CreateContext(path); // no key provided

        await middleware.InvokeAsync(ctx);

        nextCalled.Should().BeTrue();
    }
}
