// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CyberLearningOSCore",
    platforms: [.macOS(.v13), .iOS(.v17)],
    products: [.library(name: "CyberLearningOSCore", targets: ["CyberLearningOSCore"])],
    targets: [
        .target(
            name: "CyberLearningOSCore",
            path: "CyberLearningOS",
            exclude: ["ContentView.swift", "CyberLearningOSApp.swift", "LearningStore.swift", "PrivacyInfo.xcprivacy"],
            sources: ["LearningModel.swift"]
        ),
        .testTarget(
            name: "CyberLearningOSTests",
            dependencies: ["CyberLearningOSCore"],
            path: "CyberLearningOSTests"
        ),
    ]
)
