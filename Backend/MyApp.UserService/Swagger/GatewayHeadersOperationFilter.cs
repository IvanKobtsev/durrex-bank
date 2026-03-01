using Microsoft.OpenApi;
using Swashbuckle.AspNetCore.SwaggerGen;

namespace MyApp.UserService.Swagger;

/// <summary>
/// Documents the X-User-Id and X-User-Role headers that the API Gateway
/// injects into every forwarded request.
/// </summary>
public class GatewayHeadersOperationFilter : IOperationFilter
{
    public void Apply(OpenApiOperation operation, OperationFilterContext context)
    {
        operation.Parameters ??= [];

        operation.Parameters.Add(new OpenApiParameter
        {
            Name = "X-User-Id",
            In = ParameterLocation.Header,
            Required = false,
            Schema = new OpenApiSchema { Type = JsonSchemaType.Integer },
            Description = "Injected by API Gateway — authenticated user ID",
        });

        operation.Parameters.Add(new OpenApiParameter
        {
            Name = "X-User-Role",
            In = ParameterLocation.Header,
            Required = false,
            Schema = new OpenApiSchema { Type = JsonSchemaType.String },
            Description = "Injected by API Gateway — authenticated user role",
        });
    }
}
