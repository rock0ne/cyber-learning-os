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
        put("id", topic.id)
        put("title", topic.title)
        put("purpose", topic.purpose)
        put("capability", topic.capability)
        put("stage", topic.stage.name)
        put("primeGist", topic.primeGist)
        put("coreNotes", topic.coreNotes)
        put("connections", topic.connections)
        put("retrieval", topic.retrieval)
        put("application", topic.application)
        put("analystExplanation", topic.analystExplanation)
        put("leaderExplanation", topic.leaderExplanation)
        put("executiveExplanation", topic.executiveExplanation)
        put("feedback", topic.feedback)
        put("createdAt", topic.createdAt)
        put("dueAt", topic.dueAt)
        put("intervalDays", topic.intervalDays)
    }

    private fun fromJson(json: JSONObject) = LearningTopic(
        id = json.optString("id"),
        title = json.optString("title"),
        purpose = json.optString("purpose"),
        capability = json.optString("capability"),
        stage = runCatching { LearningStage.valueOf(json.optString("stage")) }
            .getOrDefault(LearningStage.PRIME),
        primeGist = json.optString("primeGist"),
        coreNotes = json.optString("coreNotes"),
        connections = json.optString("connections"),
        retrieval = json.optString("retrieval"),
        application = json.optString("application"),
        analystExplanation = json.optString("analystExplanation"),
        leaderExplanation = json.optString("leaderExplanation"),
        executiveExplanation = json.optString("executiveExplanation"),
        feedback = json.optString("feedback"),
        createdAt = json.optLong("createdAt", System.currentTimeMillis()),
        dueAt = json.optLong("dueAt"),
        intervalDays = json.optInt("intervalDays"),
    )
}
