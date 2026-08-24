import SwiftUI

struct ContentView: View {
    @StateObject private var store = LearningStore()
    @State private var creating = false
    @State private var blocked = false

    private let accent = Color(red: 0.24, green: 0.90, blue: 0.76)

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text("CYBER LEARNING OS").font(.caption.bold()).foregroundStyle(accent)
                    Text("Demonstrate understanding.\nDo not count consumption.")
                        .font(.largeTitle.bold())

                    debtCard
                    missionCard
                    Button("+ Start a purposeful topic") { creating = true }
                        .buttonStyle(.borderedProminent).tint(accent).foregroundStyle(.black)

                    if !store.topics.isEmpty {
                        Picker("Topic", selection: $store.selectedID) {
                            ForEach(store.topics) { Text($0.title).tag(Optional($0.id)) }
                        }.onChange(of: store.selectedID) { _, _ in store.save() }
                        editor
                    }

                    Divider().padding(.top)
                    Link("Method inspiration: Dr Justin Sung - My Exact 14-Step Guide To Learn Anything Faster",
                         destination: URL(string: "https://youtu.be/CQQTwvDb5xg")!)
                        .font(.footnote).foregroundStyle(.secondary)
                }
                .padding(20)
            }
            .background(Color(red: 0.03, green: 0.07, blue: 0.12))
            .sheet(isPresented: $creating) { NewTopicView(store: store, isPresented: $creating) }
            .alert("Evidence required", isPresented: $blocked) {
                Button("Continue working", role: .cancel) { }
            } message: {
                Text("Demonstrate this stage before continuing. Reading alone does not count as mastery.")
            }
        }
    }

    private var debtCard: some View {
        let debt = LearningPolicy.learningDebt(store.topics)
        let due = store.topics.filter { $0.stage == .review && ($0.dueAt ?? .distantFuture) < Date() }.count
        return Text("Learning Debt: \(debt == 0 ? "LOW" : debt < 6 ? "MODERATE" : "HIGH") (\(debt))  •  \(due) due")
            .font(.subheadline.bold()).foregroundStyle(debt == 0 ? accent : .orange)
    }

    private var missionCard: some View {
        Group {
            if let topic = LearningPolicy.nextMission(store.topics) {
                VStack(alignment: .leading, spacing: 5) {
                    Text("TODAY'S MISSION").font(.caption.bold()).foregroundStyle(accent)
                    Text("\(topic.stage.label) • \(topic.title)").font(.title3.bold())
                    Text("Capability: \(topic.capability)").foregroundStyle(.secondary)
                }
            } else {
                Text("TODAY'S MISSION\nCreate one topic with a clear capability outcome.")
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading).padding().background(.white.opacity(0.06))
        .clipShape(RoundedRectangle(cornerRadius: 14))
    }

    @ViewBuilder private var editor: some View {
        if let topic = store.selected {
            VStack(alignment: .leading, spacing: 10) {
                Text("\(topic.stage.label.uppercased()) • \(topic.title)")
                    .font(.caption.bold()).foregroundStyle(accent)
                Text(prompt(topic.stage)).font(.title2.bold())

                switch topic.stage {
                case .prime: evidenceEditor("Write the gist before detailed study", keyPath: \.primeGist)
                case .learn: evidenceEditor("Selective notes: mechanisms and relationships", keyPath: \.coreNotes)
                case .connect: evidenceEditor("What does this connect to or change?", keyPath: \.connections)
                case .retrieve: evidenceEditor("Close resources. Reconstruct from memory.", keyPath: \.retrieval)
                case .apply: evidenceEditor("Lab, logs, scenario, investigation or decision evidence", keyPath: \.application)
                case .explain:
                    evidenceEditor("Analyst: mechanism, telemetry, reasoning", keyPath: \.analystExplanation)
                    evidenceEditor("Technical leader: significance, confidence, action", keyPath: \.leaderExplanation)
                    evidenceEditor("Executive: exposure, consequence, decision", keyPath: \.executiveExplanation)
                case .feedback: evidenceEditor("What was right, what was missed, and why?", keyPath: \.feedback)
                case .review: reviewButtons
                }

                if topic.stage != .review {
                    HStack {
                        Button("Save evidence") { store.save() }.buttonStyle(.bordered)
                        Button("Save & continue") {
                            store.save()
                            if !store.advance() { blocked = true }
                        }.buttonStyle(.borderedProminent).tint(accent).foregroundStyle(.black)
                    }
                }
            }
            .padding().background(.white.opacity(0.05)).clipShape(RoundedRectangle(cornerRadius: 14))
        }
    }

    private func evidenceEditor(_ label: String, keyPath: WritableKeyPath<LearningTopic, String>) -> some View {
        VStack(alignment: .leading, spacing: 5) {
            Text(label).font(.caption).foregroundStyle(.secondary)
            TextEditor(text: binding(keyPath)).frame(minHeight: 105).padding(6)
                .background(.black.opacity(0.22)).clipShape(RoundedRectangle(cornerRadius: 9))
        }
    }

    private var reviewButtons: some View {
        VStack(alignment: .leading) {
            Text("Retrieve first, then rate demonstrated performance.").foregroundStyle(.secondary)
            HStack {
                ForEach(ReviewRating.allCases, id: \.self) { rating in
                    Button(rating.rawValue.capitalized) { store.rate(rating) }.buttonStyle(.bordered)
                }
            }
        }
    }

    private func binding(_ keyPath: WritableKeyPath<LearningTopic, String>) -> Binding<String> {
        Binding {
            guard let index = store.selectedIndex else { return "" }
            return store.topics[index][keyPath: keyPath]
        } set: { value in
            guard let index = store.selectedIndex else { return }
            store.topics[index][keyPath: keyPath] = value
        }
    }

    private func prompt(_ stage: LearningStage) -> String {
        switch stage {
        case .prime: return "What is this fundamentally about?"
        case .learn: return "Acquire selectively; do not transcribe."
        case .connect: return "Build relationships, not folders."
        case .retrieve: return "Can you reconstruct it without support?"
        case .apply: return "Use it under realistic conditions."
        case .explain: return "Preserve accuracy across three audiences."
        case .feedback: return "Compare your reasoning with the evidence."
        case .review: return "Retrieve again before rating yourself."
        }
    }
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
                Section("Purpose gate") {
                    TextField("Topic", text: $title)
                    TextField("Why am I learning this?", text: $purpose, axis: .vertical)
                    TextField("Afterward, what can I do?", text: $capability, axis: .vertical)
                }
            }
            .navigationTitle("New learning mission")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { isPresented = false } }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Start") {
                        store.add(title: title, purpose: purpose, capability: capability)
                        isPresented = false
                    }.disabled(title.trimmed.isEmpty || purpose.trimmed.isEmpty || capability.trimmed.isEmpty)
                }
            }
        }
    }
}

private extension String {
    var trimmed: String { trimmingCharacters(in: .whitespacesAndNewlines) }
}
