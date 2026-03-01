namespace MyApp.CreditService.Infrastructure;

public class CoreServiceApiKeyHandler(IConfiguration config) : DelegatingHandler
{
    protected override Task<HttpResponseMessage> SendAsync(
        HttpRequestMessage request,
        CancellationToken ct
    )
    {
        var key = config["Services:CoreService:InternalApiKey"];
        if (!string.IsNullOrEmpty(key))
            request.Headers.Add("X-Internal-Api-Key", key);
        return base.SendAsync(request, ct);
    }
}
