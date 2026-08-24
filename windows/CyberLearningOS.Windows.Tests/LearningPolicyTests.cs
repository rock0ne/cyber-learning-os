using Xunit;

namespace CyberLearningOS.Windows.Tests;

public sealed class LearningPolicyTests
{
    [Fact]
    public void Reading_does_not_clear_learning_debt()
    {
        var topic = new LearningTopic
        {
            Title = "Kerberos",
            Purpose = "Investigate identity",
            Capability = "Trace tickets",
            Stage = LearningStage.Connect,
        };
        Assert.Equal(3, LearningPolicy.LearningDebt([topic], DateTimeOffset.UnixEpoch));
    }

    [Theory]
    [InlineData(ReviewRating.Again, 1)]
    [InlineData(ReviewRating.Hard, 8)]
    [InlineData(ReviewRating.Good, 16)]
    [InlineData(ReviewRating.Strong, 24)]
    public void Review_spacing_adapts(ReviewRating rating, int expected) =>
        Assert.Equal(expected, LearningPolicy.NextInterval(8, rating));

    [Fact]
    public void Overdue_review_is_the_next_mission()
    {
        var active = new LearningTopic { Title = "OAuth", Purpose = "Identity", Capability = "Explain flows" };
        var overdue = new LearningTopic
        {
            Title = "Kerberos",
            Purpose = "Hunt",
            Capability = "Investigate",
            Stage = LearningStage.Review,
            DueAt = DateTimeOffset.UnixEpoch.AddDays(1),
        };
        Assert.Same(overdue, LearningPolicy.NextMission([active, overdue], DateTimeOffset.UnixEpoch.AddDays(2)));
    }
}
