package uk.cybertecpro.cyberlearningos

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class LearningPolicyTest {
    @Test fun reading_does_not_clear_learning_debt() {
        val topic = LearningTopic(title = "Kerberos", purpose = "Investigate identity", capability = "Trace tickets")
        topic.stage = LearningStage.CONNECT
        assertEquals(3, LearningPolicy.learningDebt(listOf(topic), now = 1))
    }

    @Test fun review_spacing_adapts_to_demonstrated_performance() {
        assertEquals(1, LearningPolicy.nextInterval(8, ReviewRating.AGAIN))
        assertEquals(8, LearningPolicy.nextInterval(8, ReviewRating.HARD))
        assertEquals(16, LearningPolicy.nextInterval(8, ReviewRating.GOOD))
        assertEquals(24, LearningPolicy.nextInterval(8, ReviewRating.STRONG))
    }

    @Test fun overdue_review_becomes_next_mission() {
        val active = LearningTopic(title = "OAuth", purpose = "Learn identity", capability = "Explain flows")
        val overdue = LearningTopic(title = "Kerberos", purpose = "Hunt", capability = "Investigate").apply {
            stage = LearningStage.REVIEW
            dueAt = 10
        }
        assertSame(overdue, LearningPolicy.nextMission(listOf(active, overdue), now = 20))
    }
}
