# iOS client

The SwiftUI client stores all learning records locally in Application Support and
declares no collected data in its privacy manifest.

Generate the Xcode project with XcodeGen:

```bash
cd ios
xcodegen generate
open CyberLearningOS.xcodeproj
```

Select your Apple development team and run on an iPhone or simulator. The portable
learning policy can be tested with `swift test` on macOS.
