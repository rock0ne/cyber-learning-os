using System.IO;
using System.Text.Json;

namespace CyberLearningOS.Windows;

public sealed class LearningStore
{
    private static readonly JsonSerializerOptions JsonOptions = new()
    {
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
        WriteIndented = true,
    };

    private readonly string _path;

    public LearningStore()
    {
        var folder = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "CyberLearningOS");
        Directory.CreateDirectory(folder);
        _path = Path.Combine(folder, "topics.json");
    }

    public IReadOnlyList<LearningTopic> Load()
    {
        try
        {
            if (!File.Exists(_path)) return [];
            var raw = File.ReadAllText(_path);
            var topics = JsonSerializer.Deserialize<List<LearningTopic>>(raw, JsonOptions) ?? [];
            using var document = JsonDocument.Parse(raw);
            for (var index = 0; index < topics.Count; index++)
            {
                var element = document.RootElement[index];
                if (!element.TryGetProperty("currentStep", out _)) MigrateLegacy(topics[index], element);
                Normalise(topics[index]);
            }
            return topics;
        }
        catch (JsonException)
        {
            return [];
        }
    }

    public void Save(IEnumerable<LearningTopic> topics)
    {
        var temp = _path + ".tmp";
        File.WriteAllText(temp, JsonSerializer.Serialize(topics, JsonOptions));
        File.Move(temp, _path, true);
    }

    private static void Normalise(LearningTopic topic)
    {
        topic.CurrentStep = Math.Clamp(topic.CurrentStep, 0, LearningGuide.Steps.Count - 1);
        topic.StepEvidence ??= [];
        while (topic.StepEvidence.Count < LearningGuide.Steps.Count) topic.StepEvidence.Add("");
        if (topic.StepEvidence.Count > LearningGuide.Steps.Count)
            topic.StepEvidence.RemoveRange(LearningGuide.Steps.Count, topic.StepEvidence.Count - LearningGuide.Steps.Count);
    }

    private static void MigrateLegacy(LearningTopic topic, JsonElement element)
    {
        var stage = element.TryGetProperty("stage", out var stageElement) ? stageElement.GetInt32() : 0;
        topic.CurrentStep = stage switch
        {
            0 => 3,
            1 => 4,
            2 => 6,
            3 => 12,
            4 => 10,
            5 => 13,
            6 => 11,
            7 => 13,
            _ => 0,
        };
        topic.Completed = stage == 7;
        topic.StepEvidence = Enumerable.Repeat("", LearningGuide.Steps.Count).ToList();
        topic.StepEvidence[0] = topic.Capability;
        topic.StepEvidence[1] = topic.Purpose;
        topic.StepEvidence[3] = Value(element, "primeGist");
        topic.StepEvidence[6] = Value(element, "connections");
        topic.StepEvidence[7] = Value(element, "coreNotes");
        topic.StepEvidence[10] = Value(element, "application");
        topic.StepEvidence[11] = Value(element, "feedback");
        topic.StepEvidence[12] = Value(element, "retrieval");
        topic.StepEvidence[13] = string.Join("\n\n", new[]
        {
            Value(element, "analystExplanation"),
            Value(element, "leaderExplanation"),
            Value(element, "executiveExplanation"),
        }.Where(value => !string.IsNullOrWhiteSpace(value)));
    }

    private static string Value(JsonElement element, string name) =>
        element.TryGetProperty(name, out var value) ? value.GetString() ?? "" : "";
}
