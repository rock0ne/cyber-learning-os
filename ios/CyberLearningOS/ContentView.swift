import SwiftUI

struct ContentView: View {
    @StateObject private var store = LearningStore()
    @State private var creating = false
    @State private var showingGuide = false
    @State private var blocked = false
    @State private var activePage = StepPage.understand

    private let accent = Color(red: 0.24, green: 0.90, blue: 0.76)

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text("CYBER LEARNING OS").font(.caption.bold()).foregroundStyle(accent)
                    Text("The complete 14-step learning guide").font(.largeTitle.bold())
                    debtCard
                    missionCard

                    HStack {
                        Button("+ Start a purposeful topic") { creating = true }
                            .buttonStyle(.borderedProminent).tint(accent).foregroundStyle(.black)
                        Button("View all 14 steps") { showingGuide = true }.buttonStyle(.bordered)
                    }

                    if !store.topics.isEmpty {
                        Picker("Topic", selection: $store.selectedID) {
                            ForEach(store.topics) { Text($0.title).tag(Optional($0.id)) }
                        }
                        editor
                    }

                    Divider().padding(.top)
                    Link("Method inspiration and credit: Dr Justin Sung - My Exact 14-Step Guide To Learn Anything Faster",
                         destination: URL(string: "https://youtu.be/CQQTwvDb5xg")!)
                        .font(.footnote).foregroundStyle(.secondary)
                }
                .padding(20)
            }
            .background(Color(red: 0.03, green: 0.07, blue: 0.12))
            .sheet(isPresented: $creating) { NewTopicView(store: store, isPresented: $creating) }
            .sheet(isPresented: $showingGuide) { RoadmapView() }
            .alert("Evidence required", isPresented: $blocked) {
                Button("Continue working", role: .cancel) { }
            } message: {
                Text("Record the evidence requested by this step before continuing.")
            }
        }
    }

    private var debtCard: some View {
        let debt = LearningPolicy.learningDebt(store.topics)
        let due = store.topics.filter { $0.completed && ($0.dueAt ?? .distantFuture) < Date() }.count
        return Text("Learning Debt: \(debt == 0 ? "LOW" : debt < 6 ? "MODERATE" : "HIGH") (\(debt))  •  \(due) due")
            .font(.subheadline.bold()).foregroundStyle(debt == 0 ? accent : .orange)
    }

    private var missionCard: some View {
        Group {
            if let topic = LearningPolicy.nextMission(store.topics) {
                VStack(alignment: .leading, spacing: 5) {
                    Text("TODAY'S MISSION").font(.caption.bold()).foregroundStyle(accent)
                    if topic.completed {
                        Text("Retrieval review • \(topic.title)").font(.title3.bold())
                        Text("Reconstruct before consulting notes or AI.").foregroundStyle(.secondary)
                    } else {
                        Text("Step \(topic.currentGuide.number) of 14 • \(topic.title)").font(.title3.bold())
                        Text(topic.currentGuide.title).foregroundStyle(.secondary)
                        Text("Capability: \(topic.capability)").foregroundStyle(.secondary)
                    }
                }
            } else {
                Text("TODAY'S MISSION\nCreate a topic and begin Step 1.")
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading).padding().background(.white.opacity(0.06))
        .clipShape(RoundedRectangle(cornerRadius: 14))
    }

    @ViewBuilder private var editor: some View {
        if let topic = store.selected {
            VStack(alignment: .leading, spacing: 12) {
                if topic.completed { reviewEditor(topic) } else { stepEditor(topic) }
            }
            .padding().background(.white.opacity(0.05)).clipShape(RoundedRectangle(cornerRadius: 14))
        }
    }

    private func stepEditor(_ topic: LearningTopic) -> some View {
        let guide = topic.currentGuide
        let lesson = TeachingContent.forStep(guide.number)
        return VStack(alignment: .leading, spacing: 12) {
            Text("STEP \(guide.number) OF 14 • \(guide.phase.uppercased())")
                .font(.caption.bold()).foregroundStyle(accent)
            Text(guide.title).font(.title2.bold())
            Picker("Lesson page", selection: $activePage) {
                ForEach(StepPage.allCases) { Text($0.rawValue).tag($0) }
            }.pickerStyle(.segmented)
            switch activePage {
            case .understand:
                guideBlock("WHAT TO DO", guide.what)
                guideBlock("WHY IT MATTERS", guide.why)
                guideBlock(lesson.technique.uppercased(), lesson.explanation)
                guideBlock("COMMON TRAP", lesson.avoid)
                guideBlock("TRANSCRIPT ANCHOR", lesson.transcriptAnchor)
            case .technique:
                guideBlock("HOW TO ACHIEVE IT", guide.how)
                diagram(lesson)
                guideBlock("CYBERSECURITY EXAMPLE", guide.cyberExample)
                guideBlock("TRY IT NOW", lesson.guidedPractice)
            case .practice:
                Text("YOUR EVIDENCE").font(.caption.bold()).foregroundStyle(accent)
                Text(guide.evidencePrompt).font(.subheadline).foregroundStyle(.secondary)
                TextEditor(text: stepBinding()).frame(minHeight: 150).padding(6)
                    .background(.black.opacity(0.22)).clipShape(RoundedRectangle(cornerRadius: 9))
                guideBlock("YOU ARE READY TO CONTINUE WHEN", guide.doneWhen)
                HStack {
                    Button("Save evidence") { store.save() }.buttonStyle(.bordered)
                    Button(guide.number == 14 ? "Complete cycle" : "Complete step \(guide.number)") {
                        store.save()
                        if store.advance() { activePage = .understand } else { blocked = true }
                    }.buttonStyle(.borderedProminent).tint(accent).foregroundStyle(.black)
                }
            }
        }
    }

    private func diagram(_ lesson: StepLesson) -> some View {
        VStack(spacing: 4) {
            Text("VISUAL MODEL • \(lesson.technique.uppercased())")
                .font(.caption.bold()).foregroundStyle(accent).frame(maxWidth: .infinity, alignment: .leading)
            ForEach(Array(lesson.diagram.enumerated()), id: \.offset) { index, node in
                Text(node).font(.subheadline.bold()).frame(maxWidth: .infinity).padding(10)
                    .background(Color.teal.opacity(0.26)).clipShape(RoundedRectangle(cornerRadius: 8))
                if index < lesson.diagram.count - 1 { Image(systemName: "arrow.down").foregroundStyle(accent) }
            }
        }.padding(12).background(.white.opacity(0.045)).clipShape(RoundedRectangle(cornerRadius: 10))
    }

    private func reviewEditor(_ topic: LearningTopic) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("14 STEPS COMPLETE • RETRIEVAL CYCLE").font(.caption.bold()).foregroundStyle(accent)
            Text(topic.title).font(.title2.bold())
            guideBlock("WHAT TO DO", "Close resources, reconstruct the model, and apply it to a fresh case before rating yourself.")
            guideBlock("HOW TO ACHIEVE IT", "1. Reproduce from memory.\n2. Apply to a new case.\n3. Compare with evidence only after committing your answer.")
            Text("RETRIEVAL EVIDENCE").font(.caption.bold()).foregroundStyle(accent)
            TextEditor(text: reviewBinding()).frame(minHeight: 150).padding(6)
                .background(.black.opacity(0.22)).clipShape(RoundedRectangle(cornerRadius: 9))
            Text("How well could you reconstruct and use it?").foregroundStyle(.secondary)
            HStack {
                ForEach(ReviewRating.allCases, id: \.self) { rating in
                    Button(rating.rawValue.capitalized) {
                        if !store.rate(rating) { blocked = true }
                    }.buttonStyle(.bordered)
                }
            }
        }
    }

    private func guideBlock(_ title: String, _ value: String) -> some View {
        VStack(alignment: .leading, spacing: 5) {
            Text(title).font(.caption.bold()).foregroundStyle(accent)
            Text(value).font(.body).textSelection(.enabled)
        }
        .frame(maxWidth: .infinity, alignment: .leading).padding(12).background(.white.opacity(0.045))
        .clipShape(RoundedRectangle(cornerRadius: 10))
    }

    private func stepBinding() -> Binding<String> {
        Binding {
            guard let index = store.selectedIndex else { return "" }
            let step = store.topics[index].currentStep
            return store.topics[index].stepEvidence[step]
        } set: { value in
            guard let index = store.selectedIndex else { return }
            let step = store.topics[index].currentStep
            store.topics[index].stepEvidence[step] = value
        }
    }

    private func reviewBinding() -> Binding<String> {
        Binding {
            guard let index = store.selectedIndex else { return "" }
            return store.topics[index].reviewEvidence
        } set: { value in
            guard let index = store.selectedIndex else { return }
            store.topics[index].reviewEvidence = value
        }
    }
}

private struct RoadmapView: View {
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            List(LearningGuide.steps) { step in
                NavigationLink {
                    ReferenceLessonView(step: step)
                } label: {
                    VStack(alignment: .leading, spacing: 5) {
                        Text("STEP \(step.number) • \(step.phase.uppercased())").font(.caption.bold()).foregroundStyle(.teal)
                        Text(step.title).font(.headline)
                        Text(step.what).foregroundStyle(.secondary)
                    }.padding(.vertical, 5)
                }
            }
            .navigationTitle("The 14-step roadmap")
            .toolbar { Button("Close") { dismiss() } }
        }
    }
}

private struct ReferenceLessonView: View {
    let step: LearningStepGuide
    @State private var page = StepPage.understand
    private let accent = Color(red: 0.24, green: 0.90, blue: 0.76)

    var body: some View {
        let lesson = TeachingContent.forStep(step.number)
        ScrollView {
            VStack(alignment: .leading, spacing: 12) {
                Text("STEP \(step.number) OF 14 • \(step.phase.uppercased())").font(.caption.bold()).foregroundStyle(accent)
                Text(step.title).font(.title.bold())
                Picker("Lesson page", selection: $page) {
                    ForEach(StepPage.allCases) { Text($0.rawValue).tag($0) }
                }.pickerStyle(.segmented)
                if page == .understand {
                    block("WHAT TO DO", step.what); block("WHY IT MATTERS", step.why)
                    block(lesson.technique.uppercased(), lesson.explanation); block("COMMON TRAP", lesson.avoid)
                    block("TRANSCRIPT ANCHOR", lesson.transcriptAnchor)
                } else if page == .technique {
                    block("HOW TO ACHIEVE IT", step.how)
                    VStack(spacing: 4) {
                        ForEach(Array(lesson.diagram.enumerated()), id: \.offset) { index, node in
                            Text(node).font(.subheadline.bold()).frame(maxWidth: .infinity).padding(10)
                                .background(Color.teal.opacity(0.24)).clipShape(RoundedRectangle(cornerRadius: 8))
                            if index < lesson.diagram.count - 1 { Image(systemName: "arrow.down").foregroundStyle(accent) }
                        }
                    }
                    block("CYBERSECURITY EXAMPLE", step.cyberExample); block("TRY IT NOW", lesson.guidedPractice)
                } else {
                    block("PRACTISE & PROVE", step.evidencePrompt); block("READY TO CONTINUE WHEN", step.doneWhen)
                    Text("Start or select a learning topic on the home page to record evidence.").foregroundStyle(.secondary)
                }
            }.padding()
        }.navigationTitle("How to learn")
    }

    private func block(_ title: String, _ value: String) -> some View {
        VStack(alignment: .leading, spacing: 5) {
            Text(title).font(.caption.bold()).foregroundStyle(accent); Text(value)
        }.frame(maxWidth: .infinity, alignment: .leading).padding(12).background(.white.opacity(0.05))
            .clipShape(RoundedRectangle(cornerRadius: 10))
    }
}

private enum StepPage: String, CaseIterable, Identifiable {
    case understand = "Understand"
    case technique = "How to do it"
    case practice = "Practise & prove"
    var id: String { rawValue }
}

private struct NewTopicView: View {
    @ObservedObject var store: LearningStore
    @Binding var isPresented: Bool
    @State private var title = ""
    @State private var purpose = ""
    @State private var capability = ""

    var body: some View {
        NavigationStack {
            Form {
                Section("Start the 14-step method") {
                    TextField("Topic", text: $title)
                    TextField("Why am I learning this?", text: $purpose, axis: .vertical)
                    TextField("What observable task will I perform?", text: $capability, axis: .vertical)
                }
            }
            .navigationTitle("New learning mission")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { isPresented = false } }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Start at Step 1") {
                        store.add(title: title, purpose: purpose, capability: capability)
                        isPresented = false
                    }.disabled(title.trimmed.isEmpty || purpose.trimmed.isEmpty || capability.trimmed.isEmpty)
                }
            }
        }
    }
}
