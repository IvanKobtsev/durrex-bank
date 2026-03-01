using System.Text;
using System.Text.Json;
using System.Text.Json.Nodes;
using Yarp.ReverseProxy.Transforms;
using Yarp.ReverseProxy.Transforms.Builder;

namespace MyApp.Gateway;

public static class SwaggerResponseTransformUtil
{
    private static readonly JsonSerializerOptions JsonOptions = new()
    {
        WriteIndented = false
    };

    private static readonly Dictionary<string, string> RouteToServerMap = new()
    {
        ["CoreRoute"] = "/core",
        ["CreditRoute"] = "/credit",
        ["UserRoute"] = "/user"
    };

    public static void AddTransformIfMatch(TransformBuilderContext builderContext)
    {
        if (!RouteToServerMap.TryGetValue(
                builderContext.Route.RouteId,
                out var serverUrl))
            return;

        builderContext.AddResponseTransform(async transformContext =>
        {
            try
            {
                var response = transformContext.ProxyResponse;

                if (response?.Content == null)
                    return;

                var json = await response.Content.ReadAsStringAsync();

                var node = JsonNode.Parse(json);

                if (node is not JsonObject obj)
                    return;

                obj["servers"] = new JsonArray
                {
                    new JsonObject { ["url"] = serverUrl }
                };

                var modified = obj.ToJsonString(JsonOptions);

                response.Content = new StringContent(
                    modified,
                    Encoding.UTF8,
                    "application/json");
            }
            catch (Exception ex)
            {
                // Console.WriteLine(ex.Message);
            }
        });
    }
}