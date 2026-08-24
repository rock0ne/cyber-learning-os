# Cyber Learning OS

An offline-first learning operating system for Android, iOS and Windows. It is
designed for people who need to consume technical material at pace without losing
context, practical judgement or the ability to explain what they know.

The governing rule is simple:

> Progress is earned through demonstrated understanding, not content consumption.

## What V1 enforces

Every topic passes through the same evidence loop:

```text
Purpose -> Prime -> Learn -> Connect -> Retrieve -> Apply -> Explain -> Feedback -> Review
```

- A topic cannot begin without a reason and an observable capability outcome.
- Each stage requires learner-authored evidence before the next stage unlocks.
- Explanation is required for analyst, technical-leader and executive audiences.
- Adaptive review intervals respond to demonstrated performance.
- Learning Debt exposes topics that were consumed but not retrieved, applied or explained.
- Today's Mission prioritises overdue retrieval and unfinished evidence loops.
- All records stay on the device in V1. There are no accounts, analytics or network calls.

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

- **Android:** download the `Cyber-Learning-OS-0.1.0-debug.apk` file and install it
  with `adb install -r <path-to-apk>`. This prototype APK uses Android's debug
  signing key and must not be mistaken for a production-store release.
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

V1 proves the offline evidence loop. It intentionally excludes AI tutoring, cloud
sync, subscriptions, public profiles and employer evidence. Those require separate
privacy, identity, security and assessment decisions. See
[the product contract](docs/PRODUCT-CONTRACT.md).

## Licence

Code is available under the [MIT License](LICENSE). The linked video and Dr Sung's
original material remain the property of their respective rights holders.
