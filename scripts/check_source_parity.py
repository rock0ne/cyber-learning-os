from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
guide_files = {
    "android": ROOT / "app/src/main/java/uk/cybertecpro/cyberlearningos/LearningModel.kt",
    "ios": ROOT / "ios/CyberLearningOS/LearningModel.swift",
    "windows": ROOT / "windows/CyberLearningOS.Windows/LearningGuide.cs",
}

step_titles = [
    "Define the learning outcome",
    "Establish purpose",
    "Schedule the learning",
    "Prime before intensive study",
    "Find the gist",
    "Protect working memory",
    "Prioritise relationships",
    "Use relational notes",
    "Choose learning order intelligently",
    "Follow the confusion compass",
    "Match learning to real performance",
    "Attempt before getting help",
    "Test strategically",
    "Reproduce real conditions",
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
