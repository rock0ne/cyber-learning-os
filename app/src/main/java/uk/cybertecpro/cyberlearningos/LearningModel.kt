package uk.cybertecpro.cyberlearningos

import java.util.UUID
import kotlin.math.max

data class LearningStepGuide(
    val number: Int,
    val phase: String,
    val title: String,
    val what: String,
    val why: String,
    val how: List<String>,
    val cyberExample: String,
    val evidencePrompt: String,
    val doneWhen: String,
)

object LearningGuide {
    const val sourceUrl = "https://youtu.be/CQQTwvDb5xg"

    val steps = listOf(
        LearningStepGuide(1, "Orient", "Measure outcomes, not speed",
            "Choose depth, delayed retention, and transfer as the scoreboard for whether learning worked.",
            "Fast coverage can create fragile familiarity and ultimately waste time through relearning.",
            listOf("Name the real performance: explain, investigate, decide, build, or brief.", "Choose an immediate depth check requiring use rather than recognition.", "Choose a delayed retention check and a transfer check using a different case.", "Record time only as a cost beside those outcomes, not as success itself."),
            "After learning Kerberos, reconstruct the flow, diagnose a failure from logs, and repeat later with a different identity case.",
            "Record an immediate depth check, delayed retention check, different-case transfer check, and time cost.",
            "Your scoreboard measures usable performance now, after delay, and in a new case—not pages or speed."),
        LearningStepGuide(2, "Orient", "Set a clear purpose and plan",
            "Connect the topic to a real problem, role, decision, or weakness in your current capability.",
            "Purpose directs attention and makes the material easier to retrieve when the real situation appears.",
            listOf("Complete: I am learning this because it will help me...", "Write two to four questions this session must answer.", "State the cost of not understanding it."),
            "I am learning OAuth attack paths so I can assess token abuse and ask better questions during an identity incident.",
            "Record your purpose, 2-4 answerable questions, and the consequence of the gap.",
            "Every planned activity can be traced back to the purpose or one of the questions."),
        LearningStepGuide(3, "Orient", "Build an actual schedule",
            "Turn study time into a bounded mission with outputs for priming, learning, connecting, performing, and explaining.",
            "A mission protects application and retrieval time from being consumed by endless reading.",
            listOf("Choose a realistic session length.", "Allocate time to prime, acquire, connect, apply, retrieve, and communicate.", "Name the artifact each block must produce."),
            "For 120 minutes: 10 prime, 35 acquire, 25 connect, 25 apply, 15 retrieve, and 10 brief.",
            "Write today's timed mission and the output expected from each block.",
            "The schedule reserves time for unaided performance and communication, not only acquisition."),
        LearningStepGuide(4, "Prime", "Plan for priming",
            "Preview the structure and vocabulary before trying to learn details.",
            "Priming creates mental hooks and reveals prerequisites, reducing working-memory overload.",
            listOf("Scan headings, diagrams, objectives, summaries, and terminology.", "Mark unfamiliar prerequisites without diving into them yet.", "Predict the shape of the topic in a rough map."),
            "Before a cloud IAM module, scan the identity flow, trust boundaries, policy types, logs, and key terms.",
            "Record the structure you expect, important terms, and missing prerequisites.",
            "You can describe the territory and its unknowns without pretending to know the details."),
        LearningStepGuide(5, "Prime", "Prime for the gist",
            "Form a provisional answer to what the topic is fundamentally about.",
            "A rough whole gives later facts somewhere meaningful to attach.",
            listOf("Spend five to ten minutes on a plain-language summary.", "Sketch a small concept map from memory.", "Label uncertain links so you can correct them later."),
            "The gist of EDR is continuous endpoint evidence plus analytics that support detection, investigation, and response - not simply antivirus.",
            "Write a plain-language gist and a rough relationship map.",
            "A newcomer could understand your summary, and uncertain parts are visibly marked."),
        LearningStepGuide(6, "Build & Connect", "Create a focused environment",
            "Use a distraction sheet to observe and remove the real triggers that break concentration.",
            "A focus environment is demonstrated by the absence of interruptions, not assumed from intuition.",
            listOf("Put a blank distraction sheet beside the active learning surface.", "Tally and name every trigger, then immediately return to the task.", "Group triggers after the session as device, environment, uncertainty, fatigue, or interruption.", "Remove or contain recurring triggers before the next block."),
            "During packet analysis, keep the PCAP, protocol reference, and question visible; park unrelated CVEs for later.",
            "Record the distraction tally, trigger categories, and the environment change for the next session.",
            "You observed a real session and converted recurring distractions into specific controls."),
        LearningStepGuide(7, "Build & Connect", "Make connections the priority",
            "Learn how concepts depend on, differ from, fail with, and reveal one another.",
            "Senior judgement comes from relationships and consequences rather than isolated definitions.",
            listOf("For each key concept ask what, why, dependencies, similarities, differences, and failure modes.", "Identify the telemetry it creates and how you would investigate it.", "Connect the technical mechanism to control and business consequences."),
            "Relate PowerShell execution policy to process creation, script-block logging, AMSI, bypass techniques, and detection limits.",
            "Describe at least three important relationships, including one failure mode and its telemetry.",
            "You can explain how changing one element affects evidence, control effectiveness, and risk."),
        LearningStepGuide(8, "Build & Connect", "Take relational notes",
            "Capture a navigable model of reasoning rather than a transcript of the source.",
            "Relational notes support investigation and transfer; copied prose mainly supports recognition.",
            listOf("Place the central question or capability—not the source title—in the centre.", "Add a small number of concepts as nodes in your own words.", "Label every connection: causes, requires, reveals, contradicts, or mitigates.", "Add contrasts, failure modes, and cross-links between branches.", "Attach evidence to claims and revise the map after testing it on a new question."),
            "Map an exposed service through vulnerability, exploitation behaviour, network evidence, detection logic, containment, and operational impact.",
            "Create a relationship chain or concept map with evidence links.",
            "The notes help answer a new question without reproducing the original source order."),
        LearningStepGuide(9, "Build & Connect", "Individualise the learning order",
            "Follow the order that resolves the problem, even when it differs from a course or chapter sequence.",
            "Problem-led ordering exposes prerequisites at the moment they become meaningful.",
            listOf("Start from the incident, question, or desired capability.", "Trace backward to missing mechanisms and forward to evidence and decisions.", "Return to the source sequence only when it is the clearest dependency path."),
            "A suspicious PowerShell event can lead through processes, Sysmon, AMSI, ATT&CK, detection, and hunting rather than a Windows textbook order.",
            "Record the learning route you chose, why it fits the problem, and any prerequisites discovered.",
            "Every detour closes a named dependency instead of becoming unbounded browsing."),
        LearningStepGuide(10, "Build & Connect", "Use confusion as a compass",
            "Convert vague confusion into a precise missing relationship and a testable hypothesis.",
            "Specific confusion directs efficient study; 'I do not get it' provides no diagnostic path.",
            listOf("Write what you expected and what actually happened.", "Name the exact link, prerequisite, or assumption that may be missing.", "State your current hypothesis and the evidence that would test it."),
            "Expected a blocked login after MFA; observed token reuse. Hypothesis: the session token was issued before the policy change and remains valid.",
            "Record expectation, observation, precise confusion, possible prerequisite, hypothesis, and test.",
            "The confusion has become a question that evidence can resolve."),
        LearningStepGuide(11, "Perform", "Match the challenge",
            "Match the style, level, and cue of practice to the performance you actually need.",
            "Recognition exercises or generous prompts cannot establish unaided complex performance.",
            listOf("Name the target challenge style: explain, query, investigate, build, decide, or present.", "Match the cognitive level: recall, application, complex problem, trade-off, or defence.", "Match the cue: supplied question, partial evidence, discussion prompts, or no cue.", "Create a practice task in the same ballpark and judge its artifact and reasoning."),
            "Investigate mixed endpoint and network evidence, state confidence, choose containment, and write a defensible incident update.",
            "Record the target style, challenge level, cue type, resulting artifact, decision, confidence, and trade-offs.",
            "Practice matches the real task's style, level, and cue and produces an inspectable output."),
        LearningStepGuide(12, "Perform", "Attempt before feedback",
            "Generate and preserve a complete response before viewing answers, hints, documentation, or AI feedback.",
            "Recognition is not recall; attempting first exposes the real model and primes correction.",
            listOf("Time-box and record your first reasoning path and conclusion.", "Then consult documentation, AI, or the reference solution as feedback.", "Compare reasoning, identify missed clues, and explain why the error occurred."),
            "Write a detection query and expected matches before asking AI to improve it; test both and explain the differences.",
            "Record the unaided attempt, help used afterward, differences found, and the cause of each important miss.",
            "The original attempt remains visible and is not overwritten by the corrected answer."),
        LearningStepGuide(13, "Retain & Communicate", "Test at the right frequency",
            "Test after enough delay to forget roughly 20–30%, then adapt the interval from the observed result.",
            "Testing too early adds little effort; testing too late turns retrieval into expensive relearning.",
            listOf("Predict a first review window—often one week to one month when earlier steps are strong.", "Retrieve without cues and estimate how much was missing.", "Lengthen the interval if almost nothing was forgotten.", "Shorten it when major relearning was required.", "Aim to test near 20–30% forgetting rather than follow a rigid schedule."),
            "Day 3 reconstruct the authentication flow; Day 7 investigate broken logs; Day 21 brief the risk; Day 30 connect it to a new attack path.",
            "Record the interval, unaided retrieval result, estimated percentage forgotten, and next-interval decision.",
            "You selected the next review from observed forgetting rather than a fixed streak or calendar."),
        LearningStepGuide(14, "Retain & Communicate", "Match the conditions that matter",
            "Rehearse the meaningful pressures of the real task—especially intensity, nervousness, scrutiny, and time.",
            "Matching every environmental detail is impractical; matching the pressures that alter performance is useful.",
            listOf("List the real task's meaningful constraints: time, scrutiny, uncertainty, interruptions, and available cues.", "Choose the two or three conditions most likely to alter performance.", "Rehearse with realistic timing and information limits.", "Add questioning or an audience when the target requires defence or presentation.", "Debrief whether judgement survived pressure, not whether the room looked similar."),
            "Under a 30-minute limit, triage partial telemetry, state what is unknown, recommend containment, and brief both SOC lead and executive.",
            "Record the conditions, evidence used, uncertainty, analyst explanation, leader briefing, executive decision request, and recommendation.",
            "You can perform and communicate accurately under conditions close to the target role."),
    )
}

enum class ReviewRating { AGAIN, HARD, GOOD, STRONG }

data class LearningTopic(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var purpose: String,
    var capability: String,
    var currentStep: Int = 0,
    var stepEvidence: MutableList<String> = MutableList(LearningGuide.steps.size) { "" },
    var completed: Boolean = false,
    var reviewEvidence: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var dueAt: Long = 0,
    var intervalDays: Int = 0,
) {
    fun currentGuide(): LearningStepGuide = LearningGuide.steps[currentStep.coerceIn(0, LearningGuide.steps.lastIndex)]
    fun currentEvidence(): String = stepEvidence.getOrElse(currentStep) { "" }
    fun setCurrentEvidence(value: String) {
        while (stepEvidence.size < LearningGuide.steps.size) stepEvidence.add("")
        stepEvidence[currentStep] = value
    }
    fun canAdvance(): Boolean = !completed && currentEvidence().isNotBlank()
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
            if (topic.completed) {
                if (topic.dueAt in 1 until now) 2 else 0
            } else {
                listOf(10, 11, 12, 13).count { topic.currentStep <= it }
            }
        }

    fun nextMission(topics: List<LearningTopic>, now: Long = System.currentTimeMillis()): LearningTopic? =
        topics.minWithOrNull(
            compareBy<LearningTopic> {
                when {
                    it.completed && it.dueAt in 1 until now -> 0
                    !it.completed -> 1
                    else -> 2
                }
            }.thenBy { if (it.dueAt == 0L) Long.MAX_VALUE else it.dueAt }
                .thenBy { it.createdAt }
        )
}
