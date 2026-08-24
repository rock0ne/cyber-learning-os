package uk.cybertecpro.cyberlearningos

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningPolicyTest {
    @Test fun guide_contains_all_fourteen_named_and_actionable_steps() {
        assertEquals(14, LearningGuide.steps.size)
        assertEquals("Define the learning outcome", LearningGuide.steps.first().title)
        assertEquals("Reproduce real conditions", LearningGuide.steps.last().title)
        assertTrue(LearningGuide.steps.all { it.what.isNotBlank() && it.how.size >= 3 && it.evidencePrompt.isNotBlank() })
    }

    @Test fun step_cannot_advance_without_learner_evidence() {
        val topic = LearningTopic(title = "Kerberos", purpose = "Investigate identity", capability = "Trace tickets")
        assertFalse(topic.canAdvance())
        topic.setCurrentEvidence("Observable task and proof")
        assertTrue(topic.canAdvance())
    }

    @Test fun reading_does_not_clear_learning_debt() {
        val topic = LearningTopic(title = "Kerberos", purpose = "Investigate identity", capability = "Trace tickets")
        topic.currentStep = 7
        assertEquals(4, LearningPolicy.learningDebt(listOf(topic), now = 1))
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
            completed = true
            dueAt = 10
        }
        assertSame(overdue, LearningPolicy.nextMission(listOf(active, overdue), now = 20))
    }
}
