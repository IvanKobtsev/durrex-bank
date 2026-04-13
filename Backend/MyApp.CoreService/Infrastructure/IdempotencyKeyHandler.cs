namespace MyApp.CoreService.Infrastructure;

/// <summary>
/// Automatically attaches an <c>Idempotency-Key</c> header (new GUID)
/// to every outgoing mutating HTTP request (POST / PUT / PATCH / DELETE).
/// </summary>
public class IdempotencyKeyHandler : DelegatingHandler
{
    private static readonly HashSet<HttpMethod> MutatingMethods =
    [
        HttpMethod.Post,
        HttpMethod.Put,
        HttpMethod.Patch,
        HttpMethod.Delete,
    ];

    protected override Task<HttpResponseMessage> SendAsync(
        HttpRequestMessage request, CancellationToken cancellationToken)
    {
        if (MutatingMethods.Contains(request.Method))
        {
            request.Headers.TryAddWithoutValidation("Idempotency-Key", Guid.NewGuid().ToString("N"));
        }

        return base.SendAsync(request, cancellationToken);
    }
}

