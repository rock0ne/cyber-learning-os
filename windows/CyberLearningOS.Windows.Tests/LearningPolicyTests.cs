using Xunit;

namespace CyberLearningOS.Windows.Tests;

public sealed class LearningPolicyTests
{
    [Fact]
    public void Guide_contains_all_fourteen_named_and_actionable_steps()
    {
        Assert.Equal(14, LearningGuide.Steps.Count);
        Assert.Equal("Define the learning outcome", LearningGuide.Steps[0].Title);
        Assert.Equal("Reproduce real conditions", LearningGuide.Steps[^1].Title);
        Assert.All(LearningGuide.Steps, step =>
        {
            Assert.False(string.IsNullOrWhiteSpace(step.What));
            Assert.Contains("1.", step.How);
            Assert.False(string.IsNullOrWhiteSpace(step.EvidencePrompt));
        });
    }

    [Fact]
    public void Step_requires_learner_evidence()
    {
        var topic = new LearningTopic { Title = "Kerberos", Purpose = "Identity", Capability = "Trace tickets" };
        Assert.False(topic.CanAdvance());
        topic.StepEvidence[0] = "Observable task and proof";
        Assert.True(topic.CanAdvance());
    }

    [Fact]
    public void Reading_does_not_clear_learning_debt()
    {
        var topic = new LearningTopic
        {
            Title = "Kerberos",
            Purpose = "Investigate identity",
            Capability = "Trace tickets",
            CurrentStep = 7,
        };
        Assert.Equal(4, LearningPolicy.LearningDebt([topic], DateTimeOffset.UnixEpoch));
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
            Completed = true,
            DueAt = DateTimeOffset.UnixEpoch.AddDays(1),
        };
        Assert.Same(overdue, LearningPolicy.NextMission([active, overdue], DateTimeOffset.UnixEpoch.AddDays(2)));
    }
}
