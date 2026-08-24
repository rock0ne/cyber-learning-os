package uk.cybertecpro.cyberlearningos

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class LearningStore(context: Context) {
    private val preferences = context.getSharedPreferences("learning-os-v1", Context.MODE_PRIVATE)

    fun load(): List<LearningTopic> {
        val raw = preferences.getString("topics", "[]") ?: "[]"
        return runCatching {
            val array = JSONArray(raw)
            List(array.length()) { index -> fromJson(array.getJSONObject(index)) }
        }.getOrDefault(emptyList())
    }

    fun save(topics: List<LearningTopic>) {
        val array = JSONArray()
        topics.forEach { array.put(toJson(it)) }
        preferences.edit().putString("topics", array.toString()).apply()
    }

    private fun toJson(topic: LearningTopic) = JSONObject().apply {
        put("schemaVersion", 2)
        put("id", topic.id)
        put("title", topic.title)
        put("purpose", topic.purpose)
        put("capability", topic.capability)
        put("currentStep", topic.currentStep)
        put("stepEvidence", JSONArray(topic.stepEvidence))
        put("completed", topic.completed)
        put("reviewEvidence", topic.reviewEvidence)
        put("createdAt", topic.createdAt)
        put("dueAt", topic.dueAt)
        put("intervalDays", topic.intervalDays)
    }

    private fun fromJson(json: JSONObject): LearningTopic {
        val evidence = if (json.has("stepEvidence")) {
            val array = json.optJSONArray("stepEvidence") ?: JSONArray()
            MutableList(LearningGuide.steps.size) { index -> if (index < array.length()) array.optString(index) else "" }
        } else migrateLegacyEvidence(json)
        val legacy = !json.has("currentStep")
        return LearningTopic(
            id = json.optString("id").ifBlank { java.util.UUID.randomUUID().toString() },
            title = json.optString("title"),
            purpose = json.optString("purpose"),
            capability = json.optString("capability"),
            currentStep = if (legacy) migrateLegacyStep(json.optString("stage")) else
                json.optInt("currentStep").coerceIn(0, LearningGuide.steps.lastIndex),
            stepEvidence = evidence,
            completed = if (legacy) json.optString("stage") == "REVIEW" else json.optBoolean("completed"),
            reviewEvidence = json.optString("reviewEvidence"),
            createdAt = json.optLong("createdAt", System.currentTimeMillis()),
            dueAt = json.optLong("dueAt"),
            intervalDays = json.optInt("intervalDays"),
        )
    }

    private fun migrateLegacyStep(stage: String): Int = when (stage) {
        "PRIME" -> 3
        "LEARN" -> 4
        "CONNECT" -> 6
        "RETRIEVE" -> 12
        "APPLY" -> 10
        "EXPLAIN" -> 13
        "FEEDBACK" -> 11
        "REVIEW" -> 13
        else -> 0
    }

    private fun migrateLegacyEvidence(json: JSONObject) = MutableList(LearningGuide.steps.size) { "" }.apply {
        this[0] = json.optString("capability")
        this[1] = json.optString("purpose")
        this[3] = json.optString("primeGist")
        this[6] = json.optString("connections")
        this[7] = json.optString("coreNotes")
        this[10] = json.optString("application")
        this[11] = json.optString("feedback")
        this[12] = json.optString("retrieval")
        this[13] = listOf(
            json.optString("analystExplanation"),
            json.optString("leaderExplanation"),
            json.optString("executiveExplanation"),
        ).filter { it.isNotBlank() }.joinToString("\n\n")
    }
}
