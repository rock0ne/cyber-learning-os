import XCTest
@testable import CyberLearningOSCore

final class LearningPolicyTests: XCTestCase {
    func testReadingDoesNotClearLearningDebt() {
        var topic = LearningTopic(title: "Kerberos", purpose: "Investigate identity", capability: "Trace tickets")
        topic.stage = .connect
        XCTAssertEqual(LearningPolicy.learningDebt([topic], now: Date(timeIntervalSince1970: 1)), 3)
    }

    func testReviewSpacingAdapts() {
        XCTAssertEqual(LearningPolicy.nextInterval(current: 8, rating: .again), 1)
        XCTAssertEqual(LearningPolicy.nextInterval(current: 8, rating: .hard), 8)
        XCTAssertEqual(LearningPolicy.nextInterval(current: 8, rating: .good), 16)
        XCTAssertEqual(LearningPolicy.nextInterval(current: 8, rating: .strong), 24)
    }
}
