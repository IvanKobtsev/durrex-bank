using MyApp.CreditService.Models;

namespace MyApp.CreditService.Services;

public static class CreditRatingCalculator
{
    public static int Calculate(IEnumerable<Credit> credits)
    {
        int score = 850;
        var now = DateTime.UtcNow;

        foreach (var credit in credits)
        {
            var overdueEntries = credit.Schedule
                .Where(e => !e.IsPaid && e.DueDate < now).ToList();

            var fullyDefaulted = overdueEntries.Count == credit.Schedule.Count
                                 && overdueEntries.Any(e => (now - e.DueDate).TotalDays > 30);

            if (fullyDefaulted)
                score -= 100;
            else
                score -= 50 * overdueEntries.Count;

            if (credit.Status == CreditStatus.Closed && credit.Schedule.All(e => e.IsPaid))
                score += 10;
        }

        return Math.Max(300, score);
    }
}
