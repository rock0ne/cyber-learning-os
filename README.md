# Cyber Learning OS

An offline-first learning operating system for Android, iOS and Windows. It is
designed for people who need to consume technical material at pace without losing
context, practical judgement or the ability to explain what they know.

The governing rule is simple:

> Progress is earned through demonstrated understanding, not content consumption.

## What V3 teaches and enforces

Every topic passes through the complete guided method:

```text
ORIENT (1-3) -> PRIME (4-5) -> BUILD & CONNECT (6-10)
-> PERFORM (11-12) -> RETAIN & COMMUNICATE (13-14) -> adaptive reviews
```

- All fourteen named steps are present; none are collapsed into generic stages.
- Every step is a small multi-page lesson: **Understand**, **How to do it**, and
  **Practise & prove**.
- Each lesson teaches a named technique, the common trap, a visual process diagram,
  detailed procedure, cybersecurity worked example, guided exercise, evidence prompt,
  source timestamp, and completion condition.
- The complete roadmap is a browsable reference; learners can study any technique before
  creating a topic.
- Every step requires learner-authored evidence before the next one unlocks.
- Step 14 requires realistic conditions and analyst, technical-leader, and executive communication.
- Adaptive review intervals respond to demonstrated performance.
- Learning Debt exposes topics that have not reached performance, unaided attempt,
  strategic testing, and real-condition evidence.
- Today's Mission prioritises overdue retrieval and unfinished evidence loops.
- All records stay on the device. There are no accounts, analytics or network calls.

## Platforms

| Platform | Technology | Local storage |
|---|---|---|
| Android | Kotlin / AndroidX | Private SharedPreferences JSON |
| iOS | SwiftUI | Application Support JSON |
| Windows | .NET 10 / WPF | `%LOCALAPPDATA%\CyberLearningOS` |

See [Android build instructions](#android), [iOS instructions](ios/README.md), and
[Windows instructions](windows/README.md).

## Install the prototype

Verified prototype packages are published on the
[GitHub Releases page](https://github.com/rock0ne/cyber-learning-os/releases).

- **Android:** download the latest `Cyber-Learning-OS-*-release.apk` file and install it
  with `adb install -r <path-to-apk>`. Public releases use the project's stable private
  signing identity so later versions can update without deleting local learning records.
- **Windows:** download and extract the `win-x64.zip` archive, then run
  `CyberLearningOS.Windows.exe`. The prototype is not yet code-signed, so Windows
  may show a SmartScreen warning.
- **iOS:** the source and verified simulator build are included, but installing on
  a physical iPhone requires Xcode and signing with your Apple developer identity.

## Android

Build the debug APK:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat test lint assembleDebug --no-daemon
```

Install it on a connected phone:

```powershell
adb install -r ".\app\build\outputs\apk\debug\app-debug.apk"
```

## Method attribution

The product design was inspired by **Dr Justin Sung's** learning framework as
presented in [My Exact 14-Step Guide To Learn Anything Faster](https://youtu.be/CQQTwvDb5xg).
The application translates those learning principles into an original software
workflow; it is not affiliated with or endorsed by Dr Sung.

Additional ideas such as the Decision Bridge, Learning Debt, three-audience cyber
communication and the evidence lifecycle are project-specific extensions for
cybersecurity learning and senior professional judgement.

## Scope and roadmap

V3 proves the offline multi-page teaching and evidence method. It intentionally excludes AI tutoring, cloud
sync, subscriptions, public profiles and employer evidence. Those require separate
privacy, identity, security and assessment decisions. See
[the product contract](docs/PRODUCT-CONTRACT.md).

## Licence

Code is available under the [MIT License](LICENSE). The linked video and Dr Sung's
original material remain the property of their respective rights holders.
