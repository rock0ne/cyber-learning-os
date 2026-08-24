namespace CyberLearningOS.Windows;

public enum ReviewRating { Again, Hard, Good, Strong }

public sealed class LearningTopic
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public string Title { get; set; } = "";
    public string Purpose { get; set; } = "";
    public string Capability { get; set; } = "";
    public int CurrentStep { get; set; }
    public List<string> StepEvidence { get; set; } = Enumerable.Repeat("", LearningGuide.Steps.Count).ToList();
    public bool Completed { get; set; }
    public string ReviewEvidence { get; set; } = "";
    public DateTimeOffset CreatedAt { get; set; } = DateTimeOffset.UtcNow;
    public DateTimeOffset? DueAt { get; set; }
    public int IntervalDays { get; set; }

    public LearningStepGuide CurrentGuide => LearningGuide.Steps[Math.Clamp(CurrentStep, 0, LearningGuide.Steps.Count - 1)];
    public bool CanAdvance() => !Completed && !string.IsNullOrWhiteSpace(StepEvidence[CurrentStep]);
    public override string ToString() => Title;
}

public static class LearningPolicy
{
    public static int NextInterval(int current, ReviewRating rating) => rating switch
    {
        ReviewRating.Again => 1,
        ReviewRating.Hard => Math.Max(2, current),
        ReviewRating.Good => Math.Max(3, current * 2),
        ReviewRating.Strong => Math.Max(7, current * 3),
        _ => 1,
    };

    public static void Schedule(LearningTopic topic, ReviewRating rating, DateTimeOffset? now = null)
    {
        topic.IntervalDays = NextInterval(topic.IntervalDays, rating);
        topic.DueAt = (now ?? DateTimeOffset.UtcNow).AddDays(topic.IntervalDays);
    }

    public static int LearningDebt(IEnumerable<LearningTopic> topics, DateTimeOffset? now = null)
    {
        var current = now ?? DateTimeOffset.UtcNow;
        return topics.Sum(topic => topic.Completed
            ? topic.DueAt is { } due && due < current ? 2 : 0
            : new[] { 10, 11, 12, 13 }.Count(step => topic.CurrentStep <= step));
    }

    public static LearningTopic? NextMission(IEnumerable<LearningTopic> topics, DateTimeOffset? now = null)
    {
        var current = now ?? DateTimeOffset.UtcNow;
        return topics.OrderBy(topic => Priority(topic, current))
            .ThenBy(topic => topic.DueAt ?? DateTimeOffset.MaxValue)
            .ThenBy(topic => topic.CreatedAt)
            .FirstOrDefault();
    }

    private static int Priority(LearningTopic topic, DateTimeOffset now) =>
        topic.Completed && topic.DueAt is { } due && due < now ? 0 : topic.Completed ? 2 : 1;
}
