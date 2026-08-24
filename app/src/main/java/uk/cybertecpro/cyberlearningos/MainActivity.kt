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
    private lateinit var editor: LinearLayout
    private lateinit var stepMeta: TextView
    private lateinit var stepTitle: TextView
    private lateinit var whatText: TextView
    private lateinit var whyText: TextView
    private lateinit var howText: TextView
    private lateinit var exampleText: TextView
    private lateinit var evidenceLabel: TextView
    private lateinit var doneText: TextView
    private lateinit var evidenceField: EditText
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
        root.addView(label("CYBER LEARNING OS", 13f, ACCENT).apply {
            setPadding(dp(20), dp(22), dp(20), dp(2))
        })
        root.addView(label("The complete 14-step learning guide", 25f, Color.WHITE).apply {
            setPadding(dp(20), 0, dp(20), dp(14))
        })

        val scroll = ScrollView(this)
        val body = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20))
        }
        scroll.addView(body)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        metrics = label("", 14f, Color.rgb(255, 198, 109))
        body.addView(metrics)
        mission = panelText("")
        body.addView(mission, margin(top = 10, bottom = 14))

        body.addView(Button(this).apply {
            text = "+ Start a purposeful topic"
            setOnClickListener { showNewTopicDialog() }
        })
        body.addView(Button(this).apply {
            text = "View all 14 steps"
            setOnClickListener { showRoadmap() }
        }, margin(top = 8))

        selector = Spinner(this)
        body.addView(selector, margin(top = 12, bottom = 12))
        selector.onItemSelectedListener = SimpleItemSelectedListener { position ->
            if (position != selectedIndex && position in topics.indices) {
                selectedIndex = position
                refreshEditor()
            }
        }

        editor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(16, 30, 48))
            setPadding(dp(16))
        }
        body.addView(editor, margin(bottom = 18))

        stepMeta = label("", 13f, ACCENT)
        stepTitle = label("", 24f, Color.WHITE)
        whatText = guideBlock("WHAT TO DO")
        whyText = guideBlock("WHY IT MATTERS")
        howText = guideBlock("HOW TO ACHIEVE IT")
        exampleText = guideBlock("CYBERSECURITY EXAMPLE")
        evidenceLabel = label("YOUR EVIDENCE", 12f, ACCENT)
        doneText = guideBlock("YOU ARE READY TO CONTINUE WHEN")
        editor.addView(stepMeta)
        editor.addView(stepTitle, margin(top = 3, bottom = 12))
        editor.addView(whatText)
        editor.addView(whyText, margin(top = 10))
        editor.addView(howText, margin(top = 10))
        editor.addView(exampleText, margin(top = 10, bottom = 14))
        editor.addView(evidenceLabel)
        evidenceField = evidenceField()
        editor.addView(evidenceField, margin(top = 5))
        editor.addView(doneText, margin(top = 10))

        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        actions.addView(Button(this).apply {
            text = "Save evidence"
            setOnClickListener { saveEvidence(showConfirmation = true) }
        }, LinearLayout.LayoutParams(0, -2, 1f))
        advance = Button(this).apply {
            text = "Complete step"
            setOnClickListener { advance() }
        }
        actions.addView(advance, LinearLayout.LayoutParams(0, -2, 1f).apply { marginStart = dp(8) })
        editor.addView(actions, margin(top = 12))

        ratings = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(label("After unaided retrieval, how well could you reconstruct and use it?", 16f, Color.WHITE))
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
        editor.addView(ratings, margin(top = 12))

        body.addView(TextView(this).apply {
            text = "Method inspiration and credit: Dr Justin Sung - My Exact 14-Step Guide To Learn Anything Faster"
            textSize = 12f
            setTextColor(Color.rgb(175, 196, 221))
            setPadding(0, dp(18), 0, dp(28))
            setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(LearningGuide.sourceUrl))) }
        })
        return root
    }

    private fun refresh() {
        selector.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            topics.map { it.title }.ifEmpty { listOf("No topics yet") },
        )
        selectedIndex = selectedIndex.coerceIn(0, (topics.size - 1).coerceAtLeast(0))
        if (topics.isNotEmpty()) selector.setSelection(selectedIndex)

        val debt = LearningPolicy.learningDebt(topics)
        val due = topics.count { it.completed && it.dueAt in 1 until System.currentTimeMillis() }
        metrics.text = "Learning Debt: ${debtLabel(debt)}  •  $due due  •  ${topics.size} topics"
        val next = LearningPolicy.nextMission(topics)
        mission.text = if (next == null) {
            "TODAY'S MISSION\nCreate a topic, define an observable outcome, and begin Step 1."
        } else if (next.completed) {
            "TODAY'S MISSION\nRETRIEVAL REVIEW  •  ${next.title}\nDue ${DateFormat.getDateInstance().format(Date(next.dueAt))}\nReconstruct before consulting notes or AI."
        } else {
            val guide = next.currentGuide()
            "TODAY'S MISSION\nSTEP ${guide.number} OF 14  •  ${next.title}\n${guide.title}\nCapability: ${next.capability}"
        }
        refreshEditor()
    }

    private fun refreshEditor() {
        val topic = topics.getOrNull(selectedIndex)
        editor.visibility = if (topic == null) View.GONE else View.VISIBLE
        if (topic == null) return

        if (topic.completed) {
            stepMeta.text = "14 STEPS COMPLETE  •  RETRIEVAL CYCLE"
            stepTitle.text = topic.title
            whatText.text = section("WHAT TO DO", "Close your resources and reconstruct the topic before rating yourself.")
            whyText.text = section("WHY IT MATTERS", "The review rating must describe demonstrated retrieval, not familiarity.")
            howText.text = section("HOW TO ACHIEVE IT", "1. Reproduce the model from memory.\n2. Apply it to a fresh case.\n3. Compare with evidence only after committing your answer.")
            exampleText.text = section("CYBERSECURITY EXAMPLE", "Rebuild the investigation path against different logs and defend the action you would take.")
            evidenceLabel.text = "RETRIEVAL EVIDENCE"
            evidenceField.hint = "What did you retrieve, apply, miss, and correct?"
            evidenceField.setText(topic.reviewEvidence)
            doneText.text = section("RATE ONLY WHEN", "You have recorded an unaided retrieval or application attempt.")
            advance.visibility = View.GONE
            ratings.visibility = View.VISIBLE
            return
        }

        val guide = topic.currentGuide()
        stepMeta.text = "STEP ${guide.number} OF 14  •  PHASE ${phaseNumber(guide.phase)} - ${guide.phase.uppercase()}"
        stepTitle.text = guide.title
        whatText.text = section("WHAT TO DO", guide.what)
        whyText.text = section("WHY IT MATTERS", guide.why)
        howText.text = section("HOW TO ACHIEVE IT", guide.how.mapIndexed { index, item -> "${index + 1}. $item" }.joinToString("\n"))
        exampleText.text = section("CYBERSECURITY EXAMPLE", guide.cyberExample)
        evidenceLabel.text = "YOUR EVIDENCE  •  ${guide.evidencePrompt}"
        evidenceField.hint = guide.evidencePrompt
        evidenceField.setText(topic.currentEvidence())
        doneText.text = section("YOU ARE READY TO CONTINUE WHEN", guide.doneWhen)
        advance.text = if (guide.number == 14) "Complete 14-step cycle" else "Complete step ${guide.number}"
        advance.visibility = View.VISIBLE
        ratings.visibility = View.GONE
    }

    private fun saveEvidence(showConfirmation: Boolean) {
        val topic = topics.getOrNull(selectedIndex) ?: return
        if (topic.completed) topic.reviewEvidence = evidenceField.text.toString().trim()
        else topic.setCurrentEvidence(evidenceField.text.toString().trim())
        store.save(topics)
        if (showConfirmation) Toast.makeText(this, "Evidence saved locally", Toast.LENGTH_SHORT).show()
    }

    private fun advance() {
        val topic = topics.getOrNull(selectedIndex) ?: return
        saveEvidence(showConfirmation = false)
        if (!topic.canAdvance()) {
            Toast.makeText(this, "Record the required evidence before continuing", Toast.LENGTH_LONG).show()
            return
        }
        if (topic.currentStep == LearningGuide.steps.lastIndex) {
            topic.completed = true
            LearningPolicy.schedule(topic, ReviewRating.HARD)
        } else topic.currentStep += 1
        store.save(topics)
        refresh()
    }

    private fun rate(rating: ReviewRating) {
        val topic = topics.getOrNull(selectedIndex) ?: return
        saveEvidence(showConfirmation = false)
        if (topic.reviewEvidence.isBlank()) {
            Toast.makeText(this, "Record the unaided retrieval before rating it", Toast.LENGTH_LONG).show()
            return
        }
        LearningPolicy.schedule(topic, rating)
        topic.reviewEvidence = ""
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
        val capability = evidenceField("What observable task will I be able to perform?")
        box.addView(title); box.addView(purpose); box.addView(capability)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Start the 14-step method")
            .setView(box)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Start at Step 1", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (listOf(title, purpose, capability).any { it.text.isBlank() }) {
                    Toast.makeText(this, "Topic, purpose and observable capability are required", Toast.LENGTH_LONG).show()
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

    private fun showRoadmap() {
        val text = LearningGuide.steps.joinToString("\n\n") { step ->
            "${step.number}. ${step.title}\n${step.phase} - ${step.what}"
        } + "\n\nSource inspiration: Dr Justin Sung. Tap About/source on the main screen for the cited video."
        AlertDialog.Builder(this)
            .setTitle("The complete 14-step roadmap")
            .setMessage(text)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun guideBlock(name: String) = label(name, 14f, Color.WHITE).apply {
        setBackgroundColor(Color.rgb(23, 40, 61))
        setPadding(dp(12))
    }

    private fun section(name: String, value: String) = "$name\n$value"

    private fun evidenceField(hint: String = ""): EditText = EditText(this).apply {
        this.hint = hint
        setHintTextColor(Color.rgb(128, 151, 178))
        setTextColor(Color.WHITE)
        setBackgroundColor(Color.rgb(23, 40, 61))
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        minLines = 5
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

    private fun phaseNumber(phase: String) = when (phase) {
        "Orient" -> 1
        "Prime" -> 2
        "Build & Connect" -> 3
        "Perform" -> 4
        else -> 5
    }

    private fun debtLabel(value: Int) = when {
        value == 0 -> "LOW (0)"
        value < 6 -> "MODERATE ($value)"
        else -> "HIGH ($value)"
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    companion object {
        val ACCENT: Int = Color.rgb(61, 229, 194)
    }
}
