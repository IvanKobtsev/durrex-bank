namespace MyApp.UserService.Auth;

public static class RoleHierarchy
{
    // Key role implicitly includes all value roles
    private static readonly Dictionary<string, string[]> Hierarchy = new()
    {
        ["Employee"] = ["Client"],
    };

    /// <summary>
    /// Returns the assigned role plus all roles it implies.
    /// E.g. "Employee" → ["Employee", "Client"]
    /// </summary>
    public static IEnumerable<string> Expand(string assignedRole)
    {
        var result = new HashSet<string> { assignedRole };
        if (Hierarchy.TryGetValue(assignedRole, out var implied))
            foreach (var r in implied) result.Add(r);
        return result;
    }
}
