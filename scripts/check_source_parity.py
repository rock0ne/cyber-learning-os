from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
files = {
    "android": ROOT / "app/src/main/java/uk/cybertecpro/cyberlearningos/LearningModel.kt",
    "ios": ROOT / "ios/CyberLearningOS/LearningModel.swift",
    "windows": ROOT / "windows/CyberLearningOS.Windows/LearningModel.cs",
}

required = ["Prime", "Learn", "Connect", "Retrieve", "Apply", "Explain", "Feedback", "Review"]
for platform, path in files.items():
    content = path.read_text(encoding="utf-8").lower()
    missing = [stage for stage in required if stage.lower() not in content]
    if missing:
        raise SystemExit(f"{platform} learning contract is missing: {', '.join(missing)}")

attribution_files = [
    ROOT / "README.md",
    ROOT / "app/src/main/java/uk/cybertecpro/cyberlearningos/MainActivity.kt",
    ROOT / "ios/CyberLearningOS/ContentView.swift",
    ROOT / "windows/CyberLearningOS.Windows/MainWindow.xaml.cs",
]
for path in attribution_files:
    if "CQQTwvDb5xg" not in path.read_text(encoding="utf-8"):
        raise SystemExit(f"source attribution missing from {path.relative_to(ROOT)}")

print("Cross-platform learning contract and attribution are present.")
