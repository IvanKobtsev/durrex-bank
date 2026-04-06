using System.Collections.Concurrent;
using System.Text.Json;
using System.Text.RegularExpressions;

namespace MyApp.MonitoringService.Services;

public sealed class SourceMapResolver(
    IWebHostEnvironment hostEnvironment,
    ILogger<SourceMapResolver> logger
)
{
    private static readonly Regex StackFrameLocationRegex = new(
        @"(?<path>(?:https?:\/\/[^\s\)]+|\/[^\s\)]+|[^\s\)]+\.m?js)):(?<line>\d+):(?<column>\d+)",
        RegexOptions.Compiled | RegexOptions.IgnoreCase
    );

    private readonly string _sourceMapsRoot = Path.Combine(
        hostEnvironment.ContentRootPath,
        "SourceMaps"
    );
    private readonly ConcurrentDictionary<string, CachedSourceMap> _cache = new(
        StringComparer.OrdinalIgnoreCase
    );

    public string? ResolveStackTrace(string? service, string? stackTrace)
    {
        if (string.IsNullOrWhiteSpace(stackTrace))
        {
            return stackTrace;
        }

        return StackFrameLocationRegex.Replace(
            stackTrace,
            match => ResolveMatch(service, match) ?? match.Value
        );
    }

    private string? ResolveMatch(string? service, Match match)
    {
        var assetPath = match.Groups["path"].Value;
        if (!int.TryParse(match.Groups["line"].Value, out var line))
        {
            return null;
        }

        if (!int.TryParse(match.Groups["column"].Value, out var column))
        {
            return null;
        }

        var sourceMapPath = FindSourceMapPath(service, assetPath);
        if (sourceMapPath is null)
        {
            return null;
        }

        var sourceMap = GetSourceMap(sourceMapPath);

        var original = sourceMap?.FindOriginalPosition(line, column);
        return original is null ? null : $"{original.Source}:{original.Line}:{original.Column}";
    }

    private string? FindSourceMapPath(string? service, string assetPath)
    {
        if (string.IsNullOrWhiteSpace(assetPath))
        {
            return null;
        }

        var relativeAssetPath = ToRelativeAssetPath(assetPath);
        if (string.IsNullOrWhiteSpace(relativeAssetPath))
        {
            return null;
        }

        var mapRelativePath = relativeAssetPath.EndsWith(".map", StringComparison.OrdinalIgnoreCase)
            ? relativeAssetPath
            : $"{relativeAssetPath}.map";
        var fileName = Path.GetFileName(mapRelativePath);
        var candidates = new List<string>();

        if (!string.IsNullOrWhiteSpace(service))
        {
            var trimmedService = service.Trim();
            candidates.Add(Path.Combine(_sourceMapsRoot, trimmedService, mapRelativePath));
            candidates.Add(Path.Combine(_sourceMapsRoot, trimmedService, "assets", fileName));
            candidates.Add(Path.Combine(_sourceMapsRoot, trimmedService, fileName));
        }

        candidates.Add(Path.Combine(_sourceMapsRoot, mapRelativePath));
        candidates.Add(Path.Combine(_sourceMapsRoot, "assets", fileName));

        foreach (var candidate in candidates)
        {
            if (File.Exists(candidate))
            {
                return candidate;
            }
        }

        return null;
    }

    private SourceMapDocument? GetSourceMap(string sourceMapPath)
    {
        try
        {
            var lastWriteAtUtc = File.GetLastWriteTimeUtc(sourceMapPath);
            if (
                _cache.TryGetValue(sourceMapPath, out var cached)
                && cached.LastWriteAtUtc == lastWriteAtUtc
            )
            {
                return cached.Document;
            }

            var json = File.ReadAllText(sourceMapPath);
            var document = SourceMapDocument.Parse(json);
            if (document is null)
            {
                return null;
            }

            _cache[sourceMapPath] = new CachedSourceMap(lastWriteAtUtc, document);
            return document;
        }
        catch (Exception ex)
        {
            logger.LogWarning(ex, "Failed to load source map from {SourceMapPath}", sourceMapPath);
            return null;
        }
    }

    private static string? ToRelativeAssetPath(string location)
    {
        var cleanedLocation = location.Trim();
        var queryIndex = cleanedLocation.IndexOfAny(['?', '#']);
        if (queryIndex >= 0)
        {
            cleanedLocation = cleanedLocation[..queryIndex];
        }

        if (
            Uri.TryCreate(cleanedLocation, UriKind.Absolute, out var absoluteUri)
            && !string.IsNullOrWhiteSpace(absoluteUri.AbsolutePath)
        )
        {
            cleanedLocation = absoluteUri.AbsolutePath;
        }

        cleanedLocation = cleanedLocation.Replace('\\', '/').TrimStart('/');
        return string.IsNullOrWhiteSpace(cleanedLocation) ? null : cleanedLocation;
    }

    private sealed record CachedSourceMap(DateTime LastWriteAtUtc, SourceMapDocument Document);

    private sealed class SourceMapDocument
    {
        private readonly Dictionary<int, List<MappingEntry>> _mappingsByGeneratedLine;
        private readonly string[] _sources;

        private SourceMapDocument(
            Dictionary<int, List<MappingEntry>> mappingsByGeneratedLine,
            string[] sources
        )
        {
            _mappingsByGeneratedLine = mappingsByGeneratedLine;
            _sources = sources;
        }

        public static SourceMapDocument? Parse(string json)
        {
            var payload = JsonSerializer.Deserialize<SourceMapPayload>(json);
            if (
                payload is null
                || payload.Sources is null
                || payload.Sources.Length == 0
                || string.IsNullOrWhiteSpace(payload.Mappings)
            )
            {
                return null;
            }

            var mappings = ParseMappings(payload.Mappings);
            if (mappings.Count == 0)
            {
                return null;
            }

            var normalizedSources = payload
                .Sources.Select(source =>
                    string.IsNullOrWhiteSpace(source) ? "unknown-source" : source
                )
                .ToArray();

            return new SourceMapDocument(mappings, normalizedSources);
        }

        public OriginalPosition? FindOriginalPosition(
            int generatedLineOneBased,
            int generatedColumnOneBased
        )
        {
            if (generatedLineOneBased <= 0 || generatedColumnOneBased <= 0)
            {
                return null;
            }

            var generatedLine = generatedLineOneBased - 1;
            var generatedColumn = generatedColumnOneBased - 1;

            if (!_mappingsByGeneratedLine.TryGetValue(generatedLine, out var entries))
            {
                return null;
            }

            var index = FindBestEntryIndex(entries, generatedColumn);
            if (index < 0)
            {
                return null;
            }

            var entry = entries[index];
            if (entry.SourceIndex < 0 || entry.SourceIndex >= _sources.Length)
            {
                return null;
            }

            return new OriginalPosition(
                _sources[entry.SourceIndex],
                entry.OriginalLine + 1,
                entry.OriginalColumn + 1
            );
        }

        private static int FindBestEntryIndex(List<MappingEntry> entries, int column)
        {
            var low = 0;
            var high = entries.Count - 1;
            var best = -1;

            while (low <= high)
            {
                var mid = low + ((high - low) / 2);
                var midColumn = entries[mid].GeneratedColumn;

                if (midColumn <= column)
                {
                    best = mid;
                    low = mid + 1;
                    continue;
                }

                high = mid - 1;
            }

            return best;
        }

        private static Dictionary<int, List<MappingEntry>> ParseMappings(string mappings)
        {
            var result = new Dictionary<int, List<MappingEntry>>();
            var generatedLine = 0;
            var generatedColumn = 0;
            var sourceIndex = 0;
            var originalLine = 0;
            var originalColumn = 0;
            var index = 0;

            while (index < mappings.Length)
            {
                var token = mappings[index];
                if (token == ';')
                {
                    generatedLine++;
                    generatedColumn = 0;
                    index++;
                    continue;
                }

                if (token == ',')
                {
                    index++;
                    continue;
                }

                var values = new List<int>();
                while (index < mappings.Length && mappings[index] != ',' && mappings[index] != ';')
                {
                    values.Add(DecodeVlq(mappings, ref index));
                }

                if (values.Count == 0)
                {
                    continue;
                }

                generatedColumn += values[0];
                if (values.Count >= 4)
                {
                    sourceIndex += values[1];
                    originalLine += values[2];
                    originalColumn += values[3];

                    if (!result.TryGetValue(generatedLine, out var lineEntries))
                    {
                        lineEntries = new List<MappingEntry>();
                        result[generatedLine] = lineEntries;
                    }

                    lineEntries.Add(
                        new MappingEntry(generatedColumn, sourceIndex, originalLine, originalColumn)
                    );
                }
            }

            return result;
        }

        private static int DecodeVlq(string mappings, ref int index)
        {
            var value = 0;
            var shift = 0;
            var hasContinuation = true;

            while (hasContinuation)
            {
                if (index >= mappings.Length)
                {
                    throw new FormatException("Unexpected end of source map mapping segment.");
                }

                var decoded = DecodeBase64(mappings[index]);
                index++;

                hasContinuation = (decoded & 32) == 32;
                value += (decoded & 31) << shift;
                shift += 5;
            }

            var isNegative = (value & 1) == 1;
            value >>= 1;
            return isNegative ? -value : value;
        }

        private static int DecodeBase64(char value)
        {
            return value switch
            {
                >= 'A' and <= 'Z' => value - 'A',
                >= 'a' and <= 'z' => value - 'a' + 26,
                >= '0' and <= '9' => value - '0' + 52,
                '+' => 62,
                '/' => 63,
                _ => throw new FormatException($"Invalid base64 VLQ character '{value}'."),
            };
        }

        private sealed record MappingEntry(
            int GeneratedColumn,
            int SourceIndex,
            int OriginalLine,
            int OriginalColumn
        );

        private sealed record SourceMapPayload
        {
            public string[]? Sources { get; init; }
            public string? Mappings { get; init; }
        }
    }

    private sealed record OriginalPosition(string Source, int Line, int Column);
}
