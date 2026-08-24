import Foundation

enum LearningStage: String, Codable, CaseIterable {
    case prime, learn, connect, retrieve, apply, explain, feedback, review

    var label: String { rawValue.capitalized }
}

enum ReviewRating: String, CaseIterable { case again, hard, good, strong }

struct LearningTopic: Codable, Identifiable, Equatable {
    var id = UUID()
    var title: String
    var purpose: String
    var capability: String
    var stage: LearningStage = .prime
    var primeGist = ""
    var coreNotes = ""
    var connections = ""
    var retrieval = ""
    var application = ""
    var analystExplanation = ""
    var leaderExplanation = ""
    var executiveExplanation = ""
    var feedback = ""
    var createdAt = Date()
    var dueAt: Date?
    var intervalDays = 0

    var canAdvance: Bool {
        switch stage {
        case .prime: return !primeGist.trimmed.isEmpty
        case .learn: return !coreNotes.trimmed.isEmpty
        case .connect: return !connections.trimmed.isEmpty
        case .retrieve: return !retrieval.trimmed.isEmpty
        case .apply: return !application.trimmed.isEmpty
        case .explain:
            return !analystExplanation.trimmed.isEmpty &&
                !leaderExplanation.trimmed.isEmpty && !executiveExplanation.trimmed.isEmpty
        case .feedback: return !feedback.trimmed.isEmpty
        case .review: return false
        }
    }
}

enum LearningPolicy {
    static func nextInterval(current: Int, rating: ReviewRating) -> Int {
        switch rating {
        case .again: return 1
        case .hard: return max(2, current)
        case .good: return max(3, current * 2)
        case .strong: return max(7, current * 3)
        }
    }

    static func schedule(_ topic: inout LearningTopic, rating: ReviewRating, now: Date = Date()) {
        topic.intervalDays = nextInterval(current: topic.intervalDays, rating: rating)
        topic.dueAt = Calendar.current.date(byAdding: .day, value: topic.intervalDays, to: now)
    }

    static func learningDebt(_ topics: [LearningTopic], now: Date = Date()) -> Int {
        topics.reduce(0) { result, topic in
            var debt = 0
            if topic.stage.index < LearningStage.retrieve.index { debt += 1 }
            if topic.stage.index < LearningStage.apply.index { debt += 1 }
            if topic.stage.index < LearningStage.explain.index { debt += 1 }
            if topic.stage == .review, let due = topic.dueAt, due < now { debt += 2 }
            return result + debt
        }
    }

    static func nextMission(_ topics: [LearningTopic], now: Date = Date()) -> LearningTopic? {
        topics.sorted { lhs, rhs in
            let left = priority(lhs, now: now)
            let right = priority(rhs, now: now)
            if left != right { return left < right }
            return (lhs.dueAt ?? .distantFuture) < (rhs.dueAt ?? .distantFuture)
        }.first
    }

    private static func priority(_ topic: LearningTopic, now: Date) -> Int {
        if topic.stage == .review, let due = topic.dueAt, due < now { return 0 }
        return topic.stage == .review ? 2 : 1
    }
}

private extension String {
    var trimmed: String { trimmingCharacters(in: .whitespacesAndNewlines) }
}

private extension LearningStage {
    var index: Int { Self.allCases.firstIndex(of: self) ?? 0 }
}
