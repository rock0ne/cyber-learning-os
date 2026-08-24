using System.Diagnostics;
using System.Windows;
using System.Windows.Controls;

namespace CyberLearningOS.Windows;

public partial class MainWindow : Window
{
    private readonly LearningStore _store = new();
    private readonly List<LearningTopic> _topics;
    private bool _rendering;
    private LessonPage _activePage = LessonPage.Understand;

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
        TopicSelector.SelectedItem = _topics.FirstOrDefault(topic => topic.Id == selectedId) ?? _topics.FirstOrDefault();

        var debt = LearningPolicy.LearningDebt(_topics);
        var due = _topics.Count(topic => topic.Completed && topic.DueAt < DateTimeOffset.UtcNow);
        DebtText.Text = $"Learning Debt: {DebtLabel(debt)} ({debt})  •  {due} due  •  {_topics.Count} topics";
        var next = LearningPolicy.NextMission(_topics);
        MissionText.Text = next is null
            ? "TODAY'S MISSION\nCreate a topic and begin Step 1."
            : next.Completed
                ? $"TODAY'S MISSION\nRETRIEVAL REVIEW  •  {next.Title}\nReconstruct before consulting notes or AI."
                : $"TODAY'S MISSION\nSTEP {next.CurrentGuide.Number} OF 14  •  {next.Title}\n{next.CurrentGuide.Title}\nCapability: {next.Capability}";
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

        StatusText.Text = "";
        if (topic.Completed)
        {
            PageTabs.Visibility = Visibility.Collapsed;
            StepMetaText.Text = "14 STEPS COMPLETE  •  RETRIEVAL CYCLE";
            StepTitleText.Text = topic.Title;
            WhatText.Text = "Close resources and reconstruct the topic before rating yourself.";
            WhyText.Text = "The rating must describe demonstrated retrieval, not familiarity.";
            HowText.Text = "1. Reproduce the model from memory.\n2. Apply it to a fresh case.\n3. Compare with evidence only after committing your answer.";
            ExampleText.Text = "Rebuild the investigation path against different logs and defend the action you would take.";
            EvidencePromptText.Text = "What did you retrieve, apply, miss, and correct?";
            EvidenceField.Text = topic.ReviewEvidence;
            DoneText.Text = "You have recorded an unaided retrieval or application attempt.";
            EditorActions.Visibility = Visibility.Collapsed;
            RatingPanel.Visibility = Visibility.Visible;
            ShowPage(LessonPage.Practice);
            return;
        }

        var guide = topic.CurrentGuide;
        var lesson = TeachingContent.ForStep(guide.Number);
        PageTabs.Visibility = Visibility.Visible;
        StepMetaText.Text = $"STEP {guide.Number} OF 14  •  {guide.Phase.ToUpperInvariant()}";
        StepTitleText.Text = guide.Title;
        WhatText.Text = guide.What;
        WhyText.Text = guide.Why;
        HowText.Text = guide.How;
        TechniqueNameText.Text = lesson.Technique.ToUpperInvariant();
        ExplanationText.Text = lesson.Explanation;
        AvoidText.Text = lesson.Avoid;
        SourceAnchorText.Text = lesson.TranscriptAnchor;
        GuidedPracticeText.Text = lesson.GuidedPractice;
        RenderDiagram(lesson);
        ExampleText.Text = guide.CyberExample;
        EvidencePromptText.Text = guide.EvidencePrompt;
        EvidenceField.Text = topic.StepEvidence[topic.CurrentStep];
        DoneText.Text = guide.DoneWhen;
        AdvanceButton.Content = guide.Number == 14 ? "Complete 14-step cycle" : $"Complete step {guide.Number}";
        EditorActions.Visibility = Visibility.Visible;
        RatingPanel.Visibility = Visibility.Collapsed;
        ShowPage(_activePage);
    }

    private void RenderDiagram(StepLesson lesson)
    {
        DiagramTitleText.Text = $"VISUAL MODEL  •  {lesson.Technique.ToUpperInvariant()}";
        DiagramPanel.Children.Clear();
        for (var index = 0; index < lesson.Diagram.Count; index++)
        {
            DiagramPanel.Children.Add(new Border
            {
                Background = new System.Windows.Media.SolidColorBrush(
                    System.Windows.Media.Color.FromRgb(12, 73, 83)),
                CornerRadius = new CornerRadius(8),
                Padding = new Thickness(12),
                Child = new TextBlock
                {
                    Text = lesson.Diagram[index],
                    TextAlignment = TextAlignment.Center,
                    FontWeight = FontWeights.SemiBold,
                },
            });
            if (index < lesson.Diagram.Count - 1)
            {
                DiagramPanel.Children.Add(new TextBlock
                {
                    Text = "↓",
                    TextAlignment = TextAlignment.Center,
                    FontSize = 22,
                    Foreground = (System.Windows.Media.Brush)FindResource("AccentBrush"),
                });
            }
        }
    }

    private void ShowPage(LessonPage page)
    {
        _activePage = page;
        UnderstandPanel.Visibility = page == LessonPage.Understand ? Visibility.Visible : Visibility.Collapsed;
        TechniquePanel.Visibility = page == LessonPage.Technique ? Visibility.Visible : Visibility.Collapsed;
        PracticePanel.Visibility = page == LessonPage.Practice ? Visibility.Visible : Visibility.Collapsed;
    }

    private void Page_Click(object sender, RoutedEventArgs e)
    {
        if (sender is Button { Tag: string value } && Enum.TryParse<LessonPage>(value, out var page))
            ShowPage(page);
    }

    private void SaveEvidence()
    {
        if (Selected is not { } topic) return;
        if (topic.Completed) topic.ReviewEvidence = EvidenceField.Text.Trim();
        else topic.StepEvidence[topic.CurrentStep] = EvidenceField.Text.Trim();
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
        if (Selected is not { } topic || !topic.CanAdvance())
        {
            StatusText.Text = "Record the evidence requested by this step before continuing.";
            return;
        }
        if (topic.CurrentStep == LearningGuide.Steps.Count - 1)
        {
            topic.Completed = true;
            LearningPolicy.Schedule(topic, ReviewRating.Hard);
        }
        else topic.CurrentStep++;
        _activePage = LessonPage.Understand;
        _store.Save(_topics);
        Render();
    }

    private void Rate_Click(object sender, RoutedEventArgs e)
    {
        SaveEvidence();
        if (Selected is not { } topic || sender is not Button { Tag: string value } ||
            !Enum.TryParse<ReviewRating>(value, out var rating)) return;
        if (string.IsNullOrWhiteSpace(topic.ReviewEvidence))
        {
            StatusText.Text = "Record the unaided retrieval before rating it.";
            return;
        }
        LearningPolicy.Schedule(topic, rating);
        topic.ReviewEvidence = "";
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

    private void Roadmap_Click(object sender, RoutedEventArgs e)
    {
        var roadmap = string.Join("\n\n", LearningGuide.Steps.Select(step =>
            $"{step.Number}. {step.Title}\n{step.Phase} - {step.What}"));
        MessageBox.Show(this, roadmap, "The complete 14-step roadmap", MessageBoxButton.OK, MessageBoxImage.Information);
    }

    private void TopicSelector_SelectionChanged(object sender, SelectionChangedEventArgs e)
    {
        if (!_rendering)
        {
            _activePage = Selected?.Completed == true ? LessonPage.Practice : LessonPage.Understand;
            RenderEditor();
        }
    }

    private void Source_Click(object sender, RoutedEventArgs e) =>
        Process.Start(new ProcessStartInfo(LearningGuide.SourceUrl) { UseShellExecute = true });

    private static string DebtLabel(int debt) => debt == 0 ? "LOW" : debt < 6 ? "MODERATE" : "HIGH";
}

internal enum LessonPage
{
    Understand,
    Technique,
    Practice,
}
