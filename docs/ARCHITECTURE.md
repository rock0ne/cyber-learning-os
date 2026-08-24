# Architecture and privacy

## Trust boundary

The current release has one trust boundary: the local application sandbox. There is no backend.

```text
14-step guide + learner evidence -> native UI -> learning policy -> app-private storage
```

Android declares no `INTERNET` permission. iOS declares no collected data in its privacy
manifest. Windows contains no HTTP client or background network service. Opening the
source-credit link explicitly hands the URL to the operating-system browser.

## Cross-platform contract

The native clients independently implement the same fourteen named steps, five phases,
guidance sections, evidence gate, adaptive review policy, overdue-first mission selection,
and source attribution. `scripts/check_source_parity.py` fails CI if a platform drops a
step, a required guidance section, or the citation identifier.

Android and Windows migrate the earlier eight-stage prototype records into the nearest
fourteen-step positions while retaining the old evidence. Each platform otherwise keeps
an independent local data file.

## Security posture

- No credentials, API keys, remote endpoints, accounts, or analytics.
- App-private storage and atomic writes where supported.
- No Android backup; no tracking or collected-data declaration on iOS.
- No arbitrary HTML, shell access, code execution, or imported active content.
- Release signing keys must never enter Git.
