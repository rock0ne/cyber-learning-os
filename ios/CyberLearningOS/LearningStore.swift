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
        if topics[index].stage == .feedback {
            LearningPolicy.schedule(&topics[index], rating: .hard)
            topics[index].stage = .review
        } else if let next = LearningStage.allCases.drop(while: { $0 != topics[index].stage }).dropFirst().first {
            topics[index].stage = next
        }
        save()
        return true
    }

    func rate(_ rating: ReviewRating) {
        guard let index = selectedIndex else { return }
        LearningPolicy.schedule(&topics[index], rating: rating)
        save()
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
        return folder.appendingPathComponent("topics.json")
    }
}
