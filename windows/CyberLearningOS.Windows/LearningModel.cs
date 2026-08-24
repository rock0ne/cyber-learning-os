namespace CyberLearningOS.Windows;

public enum LearningStage { Prime, Learn, Connect, Retrieve, Apply, Explain, Feedback, Review }
public enum ReviewRating { Again, Hard, Good, Strong }

public sealed class LearningTopic
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public string Title { get; set; } = "";
    public string Purpose { get; set; } = "";
    public string Capability { get; set; } = "";
    public LearningStage Stage { get; set; }
    public string PrimeGist { get; set; } = "";
    public string CoreNotes { get; set; } = "";
    public string Connections { get; set; } = "";
    public string Retrieval { get; set; } = "";
    public string Application { get; set; } = "";
    public string AnalystExplanation { get; set; } = "";
    public string LeaderExplanation { get; set; } = "";
    public string ExecutiveExplanation { get; set; } = "";
    public string Feedback { get; set; } = "";
    public DateTimeOffset CreatedAt { get; set; } = DateTimeOffset.UtcNow;
    public DateTimeOffset? DueAt { get; set; }
    public int IntervalDays { get; set; }

    public bool CanAdvance() => Stage switch
    {
        LearningStage.Prime => Present(PrimeGist),
        LearningStage.Learn => Present(CoreNotes),
        LearningStage.Connect => Present(Connections),
        LearningStage.Retrieve => Present(Retrieval),
        LearningStage.Apply => Present(Application),
        LearningStage.Explain => Present(AnalystExplanation) && Present(LeaderExplanation) && Present(ExecutiveExplanation),
        LearningStage.Feedback => Present(Feedback),
        _ => false,
    };

    public override string ToString() => Title;
    private static bool Present(string value) => !string.IsNullOrWhiteSpace(value);
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
        return topics.Sum(topic =>
        {
            var debt = 0;
            if (topic.Stage < LearningStage.Retrieve) debt++;
            if (topic.Stage < LearningStage.Apply) debt++;
            if (topic.Stage < LearningStage.Explain) debt++;
            if (topic.Stage == LearningStage.Review && topic.DueAt is { } due && due < current) debt += 2;
            return debt;
        });
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
        topic.Stage == LearningStage.Review && topic.DueAt is { } due && due < now ? 0 :
        topic.Stage == LearningStage.Review ? 2 : 1;
}
