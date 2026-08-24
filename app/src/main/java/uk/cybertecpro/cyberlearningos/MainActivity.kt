package uk.cybertecpro.cyberlearningos

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import java.text.DateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var store: LearningStore
    private val topics = mutableListOf<LearningTopic>()
    private var selectedIndex = 0

    private lateinit var metrics: TextView
    private lateinit var mission: TextView
    private lateinit var selector: Spinner
    private lateinit var stage: TextView
    private lateinit var prompt: TextView
    private lateinit var fieldOne: EditText
    private lateinit var fieldTwo: EditText
    private lateinit var fieldThree: EditText
    private lateinit var advance: Button
    private lateinit var ratings: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = LearningStore(this)
        topics += store.load()
        setContentView(buildUi())
        refresh()
    }

    private fun buildUi(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(7, 17, 31))
        }
        root.addView(TextView(this).apply {
            text = "CYBER LEARNING OS"
            textSize = 13f
            setTextColor(Color.rgb(61, 229, 194))
            setPadding(dp(20), dp(22), dp(20), dp(2))
        })
        root.addView(TextView(this).apply {
            text = "Demonstrate understanding.\nDo not count consumption."
            textSize = 25f
            setTextColor(Color.WHITE)
            setPadding(dp(20), 0, dp(20), dp(14))
        })

        val scroll = ScrollView(this)
        val body = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20))
        }
        scroll.addView(body)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        metrics = label("No learning debt yet", 14f, Color.rgb(255, 198, 109))
        body.addView(metrics)
        mission = panelText("")
        body.addView(mission, margin(top = 10, bottom = 14))

        body.addView(Button(this).apply {
            text = "+ Start a purposeful topic"
            setOnClickListener { showNewTopicDialog() }
        })
        selector = Spinner(this)
        body.addView(selector, margin(top = 12, bottom = 12))
        selector.onItemSelectedListener = SimpleItemSelectedListener { position ->
            if (position != selectedIndex && position in topics.indices) {
                selectedIndex = position
                refreshEditor()
            }
        }

        stage = label("", 14f, Color.rgb(61, 229, 194))
        body.addView(stage)
        prompt = label("", 19f, Color.WHITE)
        body.addView(prompt, margin(top = 4, bottom = 8))

        fieldOne = evidenceField()
        fieldTwo = evidenceField()
        fieldThree = evidenceField()
        body.addView(fieldOne)
        body.addView(fieldTwo, margin(top = 8))
        body.addView(fieldThree, margin(top = 8))

        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        actions.addView(Button(this).apply {
            text = "Save evidence"
            setOnClickListener { saveEvidence(showConfirmation = true) }
        }, LinearLayout.LayoutParams(0, -2, 1f))
        advance = Button(this).apply {
            text = "Save & continue"
            setOnClickListener { advance() }
        }
        actions.addView(advance, LinearLayout.LayoutParams(0, -2, 1f).apply { marginStart = dp(8) })
        body.addView(actions, margin(top = 12))

        ratings = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(label("How well could you reconstruct and use it?", 16f, Color.WHITE))
            addView(LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                ReviewRating.entries.forEach { rating ->
                    addView(Button(this@MainActivity).apply {
                        text = rating.name.lowercase().replaceFirstChar { it.uppercase() }
                        setOnClickListener { rate(rating) }
                    }, LinearLayout.LayoutParams(0, -2, 1f))
                }
            })
        }
        body.addView(ratings, margin(top = 12, bottom = 18))

        body.addView(TextView(this).apply {
            text = "Method inspiration: Dr Justin Sung — My Exact 14-Step Guide To Learn Anything Faster"
            textSize = 12f
            setTextColor(Color.rgb(175, 196, 221))
            setPadding(0, dp(18), 0, dp(28))
            setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(SOURCE_URL)))
            }
        })
        return root
    }

    private fun refresh() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
            topics.map { it.title }.ifEmpty { listOf("No topics yet") })
        selector.adapter = adapter
        selectedIndex = selectedIndex.coerceIn(0, (topics.size - 1).coerceAtLeast(0))
        if (topics.isNotEmpty()) selector.setSelection(selectedIndex)

        val debt = LearningPolicy.learningDebt(topics)
        val due = topics.count { it.stage == LearningStage.REVIEW && it.dueAt in 1 until System.currentTimeMillis() }
        metrics.text = "Learning Debt: ${debtLabel(debt)}  •  $due due  •  ${topics.size} topics"
        val next = LearningPolicy.nextMission(topics)
        mission.text = if (next == null) {
            "TODAY'S MISSION\nCreate one topic with a clear capability outcome."
        } else {
            val timing = if (next.stage == LearningStage.REVIEW && next.dueAt > 0)
                "Due ${DateFormat.getDateInstance().format(Date(next.dueAt))}" else "Continue the evidence loop"
            "TODAY'S MISSION\n${next.stage.label.uppercase()}  •  ${next.title}\n$timing\nCapability: ${next.capability}"
        }
        refreshEditor()
    }

    private fun refreshEditor() {
        val topic = topics.getOrNull(selectedIndex)
        listOf(stage, prompt, fieldOne, fieldTwo, fieldThree, advance, ratings).forEach {
            it.visibility = if (topic == null) View.GONE else View.VISIBLE
        }
        if (topic == null) return
        stage.text = "${topic.stage.label.uppercase()}  •  ${topic.title}"
        prompt.text = promptFor(topic)
        fieldTwo.visibility = View.GONE
        fieldThree.visibility = View.GONE
        ratings.visibility = if (topic.stage == LearningStage.REVIEW) View.VISIBLE else View.GONE
        advance.visibility = if (topic.stage == LearningStage.REVIEW) View.GONE else View.VISIBLE
        fieldOne.visibility = if (topic.stage == LearningStage.REVIEW) View.GONE else View.VISIBLE

        when (topic.stage) {
            LearningStage.PRIME -> setFields("Write the gist before detailed study", topic.primeGist)
            LearningStage.LEARN -> setFields("Selective notes: mechanisms and relationships", topic.coreNotes)
            LearningStage.CONNECT -> setFields("What does this connect to or change?", topic.connections)
            LearningStage.RETRIEVE -> setFields("Close resources. Reconstruct from memory.", topic.retrieval)
            LearningStage.APPLY -> setFields("Lab, logs, scenario, investigation or decision evidence", topic.application)
            LearningStage.EXPLAIN -> {
                setFields("Analyst: mechanism, telemetry, reasoning", topic.analystExplanation)
                fieldTwo.visibility = View.VISIBLE
                fieldThree.visibility = View.VISIBLE
                fieldTwo.hint = "Technical leader: significance, confidence, action"
                fieldTwo.setText(topic.leaderExplanation)
                fieldThree.hint = "Executive: exposure, consequence, decision required"
                fieldThree.setText(topic.executiveExplanation)
            }
            LearningStage.FEEDBACK -> setFields("What was right, what was missed, and why?", topic.feedback)
            LearningStage.REVIEW -> Unit
        }
    }

    private fun promptFor(topic: LearningTopic): String = when (topic.stage) {
        LearningStage.PRIME -> "What is this fundamentally about?"
        LearningStage.LEARN -> "Acquire selectively; do not transcribe."
        LearningStage.CONNECT -> "Build relationships, not folders."
        LearningStage.RETRIEVE -> "Can you reconstruct it without support?"
        LearningStage.APPLY -> "Use it under realistic conditions."
        LearningStage.EXPLAIN -> "Preserve accuracy across three audiences."
        LearningStage.FEEDBACK -> "Compare your reasoning with the evidence."
        LearningStage.REVIEW -> "Retrieve again before rating yourself."
    }

    private fun saveEvidence(showConfirmation: Boolean) {
        val topic = topics.getOrNull(selectedIndex) ?: return
        when (topic.stage) {
            LearningStage.PRIME -> topic.primeGist = fieldOne.text.toString().trim()
            LearningStage.LEARN -> topic.coreNotes = fieldOne.text.toString().trim()
            LearningStage.CONNECT -> topic.connections = fieldOne.text.toString().trim()
            LearningStage.RETRIEVE -> topic.retrieval = fieldOne.text.toString().trim()
            LearningStage.APPLY -> topic.application = fieldOne.text.toString().trim()
            LearningStage.EXPLAIN -> {
                topic.analystExplanation = fieldOne.text.toString().trim()
                topic.leaderExplanation = fieldTwo.text.toString().trim()
                topic.executiveExplanation = fieldThree.text.toString().trim()
            }
            LearningStage.FEEDBACK -> topic.feedback = fieldOne.text.toString().trim()
            LearningStage.REVIEW -> Unit
        }
        store.save(topics)
        if (showConfirmation) Toast.makeText(this, "Evidence saved locally", Toast.LENGTH_SHORT).show()
    }

    private fun advance() {
        val topic = topics.getOrNull(selectedIndex) ?: return
        saveEvidence(showConfirmation = false)
        if (!topic.canAdvance()) {
            Toast.makeText(this, "Demonstrate this stage before continuing", Toast.LENGTH_LONG).show()
            return
        }
        topic.stage = if (topic.stage == LearningStage.FEEDBACK) {
            LearningPolicy.schedule(topic, ReviewRating.HARD)
            LearningStage.REVIEW
        } else LearningStage.entries[topic.stage.ordinal + 1]
        store.save(topics)
        refresh()
    }

    private fun rate(rating: ReviewRating) {
        val topic = topics.getOrNull(selectedIndex) ?: return
        LearningPolicy.schedule(topic, rating)
        store.save(topics)
        Toast.makeText(this, "Next review in ${topic.intervalDays} day(s)", Toast.LENGTH_SHORT).show()
        refresh()
    }

    private fun showNewTopicDialog() {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20))
        }
        val title = evidenceField("Topic")
        val purpose = evidenceField("Why am I learning this?")
        val capability = evidenceField("Afterward, what can I do?")
        box.addView(title); box.addView(purpose); box.addView(capability)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Purpose gate")
            .setView(box)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Start", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (listOf(title, purpose, capability).any { it.text.isBlank() }) {
                    Toast.makeText(this, "Topic, purpose and capability are required", Toast.LENGTH_LONG).show()
                } else {
                    topics += LearningTopic(
                        title = title.text.toString().trim(),
                        purpose = purpose.text.toString().trim(),
                        capability = capability.text.toString().trim(),
                    )
                    selectedIndex = topics.lastIndex
                    store.save(topics)
                    dialog.dismiss()
                    refresh()
                }
            }
        }
        dialog.show()
    }

    private fun setFields(hint: String, value: String) {
        fieldOne.hint = hint
        fieldOne.setText(value)
        fieldTwo.setText("")
        fieldThree.setText("")
    }

    private fun evidenceField(hint: String = ""): EditText = EditText(this).apply {
        this.hint = hint
        setHintTextColor(Color.rgb(128, 151, 178))
        setTextColor(Color.WHITE)
        setBackgroundColor(Color.rgb(23, 40, 61))
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        minLines = 3
        gravity = Gravity.TOP
        setPadding(dp(12))
    }

    private fun label(value: String, size: Float, color: Int) = TextView(this).apply {
        text = value; textSize = size; setTextColor(color)
    }

    private fun panelText(value: String) = label(value, 15f, Color.WHITE).apply {
        setBackgroundColor(Color.rgb(16, 30, 48)); setPadding(dp(16))
    }

    private fun margin(top: Int = 0, bottom: Int = 0) = LinearLayout.LayoutParams(-1, -2).apply {
        topMargin = dp(top); bottomMargin = dp(bottom)
    }

    private fun debtLabel(value: Int) = when {
        value == 0 -> "LOW (0)"
        value < 6 -> "MODERATE ($value)"
        else -> "HIGH ($value)"
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    companion object {
        const val SOURCE_URL = "https://youtu.be/CQQTwvDb5xg"
    }
}
