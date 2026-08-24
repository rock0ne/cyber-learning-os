package uk.cybertecpro.cyberlearningos

import java.util.UUID
import kotlin.math.max

enum class LearningStage(val label: String) {
    PRIME("Prime"),
    LEARN("Learn"),
    CONNECT("Connect"),
    RETRIEVE("Retrieve"),
    APPLY("Apply"),
    EXPLAIN("Explain"),
    FEEDBACK("Feedback"),
    REVIEW("Review")
}

enum class ReviewRating { AGAIN, HARD, GOOD, STRONG }

data class LearningTopic(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var purpose: String,
    var capability: String,
    var stage: LearningStage = LearningStage.PRIME,
    var primeGist: String = "",
    var coreNotes: String = "",
    var connections: String = "",
    var retrieval: String = "",
    var application: String = "",
    var analystExplanation: String = "",
    var leaderExplanation: String = "",
    var executiveExplanation: String = "",
    var feedback: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var dueAt: Long = 0,
    var intervalDays: Int = 0,
) {
    fun canAdvance(): Boolean = when (stage) {
        LearningStage.PRIME -> primeGist.isNotBlank()
        LearningStage.LEARN -> coreNotes.isNotBlank()
        LearningStage.CONNECT -> connections.isNotBlank()
        LearningStage.RETRIEVE -> retrieval.isNotBlank()
        LearningStage.APPLY -> application.isNotBlank()
        LearningStage.EXPLAIN -> analystExplanation.isNotBlank() &&
            leaderExplanation.isNotBlank() && executiveExplanation.isNotBlank()
        LearningStage.FEEDBACK -> feedback.isNotBlank()
        LearningStage.REVIEW -> false
    }
}

object LearningPolicy {
    const val DAY_MS = 86_400_000L

    fun nextInterval(current: Int, rating: ReviewRating): Int = when (rating) {
        ReviewRating.AGAIN -> 1
        ReviewRating.HARD -> max(2, current)
        ReviewRating.GOOD -> max(3, current * 2)
        ReviewRating.STRONG -> max(7, current * 3)
    }

    fun schedule(topic: LearningTopic, rating: ReviewRating, now: Long = System.currentTimeMillis()) {
        topic.intervalDays = nextInterval(topic.intervalDays, rating)
        topic.dueAt = now + topic.intervalDays * DAY_MS
    }

    fun learningDebt(topics: List<LearningTopic>, now: Long = System.currentTimeMillis()): Int =
        topics.sumOf { topic ->
            var debt = 0
            if (topic.stage.ordinal < LearningStage.RETRIEVE.ordinal) debt += 1
            if (topic.stage.ordinal < LearningStage.APPLY.ordinal) debt += 1
            if (topic.stage.ordinal < LearningStage.EXPLAIN.ordinal) debt += 1
            if (topic.stage == LearningStage.REVIEW && topic.dueAt in 1 until now) debt += 2
            debt
        }

    fun nextMission(topics: List<LearningTopic>, now: Long = System.currentTimeMillis()): LearningTopic? =
        topics.minWithOrNull(
            compareBy<LearningTopic> {
                when {
                    it.stage == LearningStage.REVIEW && it.dueAt in 1 until now -> 0
                    it.stage != LearningStage.REVIEW -> 1
                    else -> 2
                }
            }.thenBy { if (it.dueAt == 0L) Long.MAX_VALUE else it.dueAt }
                .thenBy { it.createdAt }
        )
}
