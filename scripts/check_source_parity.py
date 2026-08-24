from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
guide_files = {
    "android": ROOT / "app/src/main/java/uk/cybertecpro/cyberlearningos/LearningModel.kt",
    "ios": ROOT / "ios/CyberLearningOS/LearningModel.swift",
    "windows": ROOT / "windows/CyberLearningOS.Windows/LearningGuide.cs",
}
teaching_files = {
    "android": ROOT / "app/src/main/java/uk/cybertecpro/cyberlearningos/TeachingContent.kt",
    "ios": ROOT / "ios/CyberLearningOS/TeachingContent.swift",
    "windows": ROOT / "windows/CyberLearningOS.Windows/TeachingContent.cs",
}

step_titles = [
    "Measure outcomes, not speed",
    "Set a clear purpose and plan",
    "Build an actual schedule",
    "Plan for priming",
    "Prime for the gist",
    "Create a focused environment",
    "Make connections the priority",
    "Take relational notes",
    "Individualise the learning order",
    "Use confusion as a compass",
    "Match the challenge",
    "Attempt before feedback",
    "Test at the right frequency",
    "Match the conditions that matter",
]

techniques = [
    "Outcome scoreboard",
    "Purpose-and-plan canvas",
    "Time-block design",
    "Priming preview",
    "Gist map",
    "Distraction sheet",
    "Schema-building questions",
    "Relational note map",
    "Relevance-led route",
    "Confusion compass",
    "Challenge matcher",
    "Attempt-feedback boundary",
    "Forgetting calibration",
    "Pressure rehearsal",
]

for platform, path in guide_files.items():
    content = path.read_text(encoding="utf-8")
    missing = [title for title in step_titles if title not in content]
    if missing:
        raise SystemExit(f"{platform} 14-step guide is missing: {', '.join(missing)}")
    for required_guidance in ["WHAT TO DO", "WHY IT MATTERS", "HOW TO ACHIEVE IT", "CYBERSECURITY EXAMPLE"]:
        ui_paths = {
            "android": ROOT / "app/src/main/java/uk/cybertecpro/cyberlearningos/MainActivity.kt",
            "ios": ROOT / "ios/CyberLearningOS/ContentView.swift",
            "windows": ROOT / "windows/CyberLearningOS.Windows/MainWindow.xaml",
        }
        if required_guidance not in ui_paths[platform].read_text(encoding="utf-8"):
            raise SystemExit(f"{platform} UI is missing guidance section: {required_guidance}")

for platform, path in teaching_files.items():
    content = path.read_text(encoding="utf-8")
    missing = [technique for technique in techniques if technique not in content]
    if missing:
        raise SystemExit(f"{platform} teaching content is missing: {', '.join(missing)}")
    for phrase in ["transcript", "guidedPractice", "Diagram"]:
        if phrase.lower() not in content.lower():
            raise SystemExit(f"{platform} teaching content is missing contract marker: {phrase}")

attribution_files = [
    ROOT / "README.md",
    ROOT / "app/src/main/java/uk/cybertecpro/cyberlearningos/LearningModel.kt",
    ROOT / "ios/CyberLearningOS/LearningModel.swift",
    ROOT / "windows/CyberLearningOS.Windows/LearningGuide.cs",
]
for path in attribution_files:
    if "CQQTwvDb5xg" not in path.read_text(encoding="utf-8"):
        raise SystemExit(f"source attribution missing from {path.relative_to(ROOT)}")

print("All three clients contain the complete guided 14-step contract and attribution.")
