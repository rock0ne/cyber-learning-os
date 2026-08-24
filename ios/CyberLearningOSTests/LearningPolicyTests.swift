import XCTest
@testable import CyberLearningOSCore

final class LearningPolicyTests: XCTestCase {
    func testGuideContainsAllFourteenNamedActionableSteps() {
        XCTAssertEqual(LearningGuide.steps.count, 14)
        XCTAssertEqual(LearningGuide.steps.first?.title, "Measure outcomes, not speed")
        XCTAssertEqual(LearningGuide.steps.last?.title, "Match the conditions that matter")
        XCTAssertTrue(LearningGuide.steps.allSatisfy {
            !$0.what.isEmpty && $0.how.contains("1.") && !$0.evidencePrompt.isEmpty
        })
        XCTAssertEqual(TeachingContent.lessons.count, 14)
        XCTAssertTrue(TeachingContent.lessons.allSatisfy {
            !$0.explanation.isEmpty && !$0.avoid.isEmpty && $0.diagram.count >= 4 &&
                !$0.guidedPractice.isEmpty && !$0.transcriptAnchor.isEmpty
        })
    }

    func testStepRequiresLearnerEvidence() {
        var topic = LearningTopic(title: "Kerberos", purpose: "Identity", capability: "Trace tickets")
        XCTAssertFalse(topic.canAdvance)
        topic.stepEvidence[0] = "Observable task and proof"
        XCTAssertTrue(topic.canAdvance)
    }

    func testReadingDoesNotClearLearningDebt() {
        var topic = LearningTopic(title: "Kerberos", purpose: "Investigate identity", capability: "Trace tickets")
        topic.currentStep = 7
        XCTAssertEqual(LearningPolicy.learningDebt([topic], now: Date(timeIntervalSince1970: 1)), 4)
    }

    func testReviewSpacingAdapts() {
        XCTAssertEqual(LearningPolicy.nextInterval(current: 8, rating: .again), 1)
        XCTAssertEqual(LearningPolicy.nextInterval(current: 8, rating: .hard), 8)
        XCTAssertEqual(LearningPolicy.nextInterval(current: 8, rating: .good), 16)
        XCTAssertEqual(LearningPolicy.nextInterval(current: 8, rating: .strong), 24)
    }
}
