# Windows client

The WPF client stores learning records in `%LOCALAPPDATA%\CyberLearningOS\topics.json`.
It performs no network requests; the only external action is the user-initiated source-credit link.

```powershell
dotnet test .\windows\CyberLearningOS.Windows.slnx -c Release
dotnet publish .\windows\CyberLearningOS.Windows\CyberLearningOS.Windows.csproj `
  -c Release -r win-x64 --self-contained true -p:PublishSingleFile=true `
  -p:DebugType=None -p:DebugSymbols=false -o .\windows\artifacts\win-x64
```
