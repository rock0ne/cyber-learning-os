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
            return File.Exists(_path)
                ? JsonSerializer.Deserialize<List<LearningTopic>>(File.ReadAllText(_path), JsonOptions) ?? []
                : [];
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
}
