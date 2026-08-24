import Foundation

@MainActor
final class LearningStore: ObservableObject {
    @Published var topics: [LearningTopic] = []
    @Published var selectedID: LearningTopic.ID?

    init() {
        load()
        selectedID = topics.first?.id
    }

    var selectedIndex: Int? { topics.firstIndex { $0.id == selectedID } }
    var selected: LearningTopic? { selectedIndex.map { topics[$0] } }

    func add(title: String, purpose: String, capability: String) {
        let topic = LearningTopic(title: title, purpose: purpose, capability: capability)
        topics.append(topic)
        selectedID = topic.id
        save()
    }

    func advance() -> Bool {
        guard let index = selectedIndex, topics[index].canAdvance else { return false }
        if topics[index].currentStep == LearningGuide.steps.count - 1 {
            topics[index].completed = true
            LearningPolicy.schedule(&topics[index], rating: .hard)
        } else {
            topics[index].currentStep += 1
        }
        save()
        return true
    }

    func rate(_ rating: ReviewRating) -> Bool {
        guard let index = selectedIndex, !topics[index].reviewEvidence.trimmed.isEmpty else { return false }
        LearningPolicy.schedule(&topics[index], rating: rating)
        topics[index].reviewEvidence = ""
        save()
        return true
    }

    func save() {
        guard let data = try? JSONEncoder().encode(topics) else { return }
        try? data.write(to: storageURL, options: .atomic)
    }

    private func load() {
        guard let data = try? Data(contentsOf: storageURL),
              let decoded = try? JSONDecoder().decode([LearningTopic].self, from: data) else { return }
        topics = decoded
    }

    private var storageURL: URL {
        let base = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask).first!
        let folder = base.appendingPathComponent("CyberLearningOS", isDirectory: true)
        try? FileManager.default.createDirectory(at: folder, withIntermediateDirectories: true)
        return folder.appendingPathComponent("topics-v2.json")
    }
}
