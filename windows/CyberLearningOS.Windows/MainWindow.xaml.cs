using System.Diagnostics;
using System.Windows;
using System.Windows.Controls;

namespace CyberLearningOS.Windows;

public partial class MainWindow : Window
{
    private const string SourceUrl = "https://youtu.be/CQQTwvDb5xg";
    private readonly LearningStore _store = new();
    private readonly List<LearningTopic> _topics;
    private bool _rendering;

    public MainWindow()
    {
        InitializeComponent();
        _topics = [.. _store.Load()];
        Render();
    }

    private LearningTopic? Selected => TopicSelector.SelectedItem as LearningTopic;

    private void Render()
    {
        _rendering = true;
        var selectedId = Selected?.Id;
        TopicSelector.ItemsSource = null;
        TopicSelector.ItemsSource = _topics;
        TopicSelector.SelectedItem = _topics.FirstOrDefault(t => t.Id == selectedId) ?? _topics.FirstOrDefault();

        var debt = LearningPolicy.LearningDebt(_topics);
        var due = _topics.Count(t => t.Stage == LearningStage.Review && t.DueAt < DateTimeOffset.UtcNow);
        DebtText.Text = $"Learning Debt: {DebtLabel(debt)} ({debt})  •  {due} due  •  {_topics.Count} topics";
        var next = LearningPolicy.NextMission(_topics);
        MissionText.Text = next is null
            ? "TODAY'S MISSION\nCreate one topic with a clear capability outcome."
            : $"TODAY'S MISSION\n{next.Stage.ToString().ToUpperInvariant()}  •  {next.Title}\nCapability: {next.Capability}";
        _rendering = false;
        RenderEditor();
    }

    private void RenderEditor()
    {
        var topic = Selected;
        EditorPanel.Visibility = topic is null ? Visibility.Collapsed : Visibility.Visible;
        PurposeText.Text = topic?.Purpose ?? "";
        CapabilityText.Text = topic?.Capability ?? "";
        if (topic is null) return;

        StageText.Text = $"{topic.Stage.ToString().ToUpperInvariant()}  •  {topic.Title}";
        PromptText.Text = Prompt(topic.Stage);
        FieldTwo.Visibility = FieldThree.Visibility = Visibility.Collapsed;
        FieldTwoLabel.Visibility = FieldThreeLabel.Visibility = Visibility.Collapsed;
        RatingPanel.Visibility = topic.Stage == LearningStage.Review ? Visibility.Visible : Visibility.Collapsed;
        EditorActions.Visibility = topic.Stage == LearningStage.Review ? Visibility.Collapsed : Visibility.Visible;
        FieldOne.Visibility = FieldOneLabel.Visibility = topic.Stage == LearningStage.Review ? Visibility.Collapsed : Visibility.Visible;
        StatusText.Text = "";

        switch (topic.Stage)
        {
            case LearningStage.Prime: SetOne("Write the gist before detailed study", topic.PrimeGist); break;
            case LearningStage.Learn: SetOne("Selective notes: mechanisms and relationships", topic.CoreNotes); break;
            case LearningStage.Connect: SetOne("What does this connect to or change?", topic.Connections); break;
            case LearningStage.Retrieve: SetOne("Close resources. Reconstruct from memory.", topic.Retrieval); break;
            case LearningStage.Apply: SetOne("Lab, logs, scenario, investigation or decision evidence", topic.Application); break;
            case LearningStage.Explain:
                SetOne("Analyst: mechanism, telemetry and technical reasoning", topic.AnalystExplanation);
                FieldTwo.Visibility = FieldThree.Visibility = Visibility.Visible;
                FieldTwoLabel.Visibility = FieldThreeLabel.Visibility = Visibility.Visible;
                FieldTwoLabel.Text = "Technical leader: significance, confidence, dependencies and action";
                FieldThreeLabel.Text = "Executive: exposure, consequence, urgency and decision required";
                FieldTwo.Text = topic.LeaderExplanation;
                FieldThree.Text = topic.ExecutiveExplanation;
                break;
            case LearningStage.Feedback: SetOne("What was right, what was missed, and why?", topic.Feedback); break;
        }
    }

    private void SetOne(string label, string value)
    {
        FieldOneLabel.Text = label;
        FieldOne.Text = value;
        FieldTwo.Text = FieldThree.Text = "";
    }

    private void SaveEvidence()
    {
        var topic = Selected;
        if (topic is null) return;
        switch (topic.Stage)
        {
            case LearningStage.Prime: topic.PrimeGist = FieldOne.Text.Trim(); break;
            case LearningStage.Learn: topic.CoreNotes = FieldOne.Text.Trim(); break;
            case LearningStage.Connect: topic.Connections = FieldOne.Text.Trim(); break;
            case LearningStage.Retrieve: topic.Retrieval = FieldOne.Text.Trim(); break;
            case LearningStage.Apply: topic.Application = FieldOne.Text.Trim(); break;
            case LearningStage.Explain:
                topic.AnalystExplanation = FieldOne.Text.Trim();
                topic.LeaderExplanation = FieldTwo.Text.Trim();
                topic.ExecutiveExplanation = FieldThree.Text.Trim();
                break;
            case LearningStage.Feedback: topic.Feedback = FieldOne.Text.Trim(); break;
        }
        _store.Save(_topics);
    }

    private void Save_Click(object sender, RoutedEventArgs e)
    {
        SaveEvidence();
        StatusText.Text = "Evidence saved locally.";
    }

    private void Advance_Click(object sender, RoutedEventArgs e)
    {
        SaveEvidence();
        var topic = Selected;
        if (topic is null || !topic.CanAdvance())
        {
            StatusText.Text = "Demonstrate this stage before continuing. Reading alone does not count as mastery.";
            return;
        }
        if (topic.Stage == LearningStage.Feedback)
        {
            LearningPolicy.Schedule(topic, ReviewRating.Hard);
            topic.Stage = LearningStage.Review;
        }
        else topic.Stage++;
        _store.Save(_topics);
        Render();
    }

    private void Rate_Click(object sender, RoutedEventArgs e)
    {
        if (Selected is not { } topic || sender is not Button { Tag: string value } ||
            !Enum.TryParse<ReviewRating>(value, out var rating)) return;
        LearningPolicy.Schedule(topic, rating);
        _store.Save(_topics);
        Render();
        StatusText.Text = $"Next review in {topic.IntervalDays} day(s).";
    }

    private void NewTopic_Click(object sender, RoutedEventArgs e)
    {
        var dialog = new NewTopicWindow { Owner = this };
        if (dialog.ShowDialog() != true) return;
        var topic = new LearningTopic
        {
            Title = dialog.TopicTitle,
            Purpose = dialog.Purpose,
            Capability = dialog.Capability,
        };
        _topics.Add(topic);
        _store.Save(_topics);
        Render();
        TopicSelector.SelectedItem = topic;
    }

    private void TopicSelector_SelectionChanged(object sender, SelectionChangedEventArgs e)
    {
        if (!_rendering) RenderEditor();
    }

    private void Source_Click(object sender, RoutedEventArgs e) =>
        Process.Start(new ProcessStartInfo(SourceUrl) { UseShellExecute = true });

    private static string Prompt(LearningStage stage) => stage switch
    {
        LearningStage.Prime => "What is this fundamentally about?",
        LearningStage.Learn => "Acquire selectively; do not transcribe.",
        LearningStage.Connect => "Build relationships, not folders.",
        LearningStage.Retrieve => "Can you reconstruct it without support?",
        LearningStage.Apply => "Use it under realistic conditions.",
        LearningStage.Explain => "Preserve accuracy across three audiences.",
        LearningStage.Feedback => "Compare your reasoning with the evidence.",
        _ => "Retrieve again before rating yourself.",
    };

    private static string DebtLabel(int debt) => debt == 0 ? "LOW" : debt < 6 ? "MODERATE" : "HIGH";
}
