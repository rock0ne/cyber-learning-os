# Architecture and privacy

## Trust boundary

V1 has one trust boundary: the local application sandbox. There is no backend.

```text
Learner input -> native UI -> shared learning policy -> app-private JSON storage
```

Android declares no `INTERNET` permission. iOS declares no collected data in its privacy
manifest. Windows contains no HTTP client or background network service. Selecting the
source-credit link explicitly hands the URL to the operating system browser.

## Cross-platform contract

The three native clients implement the same:

- stage enum and ordering;
- purpose gate;
- required evidence rules;
- Learning Debt weights;
- adaptive interval calculation;
- overdue-first mission selection;
- source attribution.

Each platform keeps an independent local data file in V1. The schemas intentionally use
the same field names so an encrypted migration/export format can be introduced later.

## Security posture

- No credentials, API keys or remote endpoints.
- App-private storage and atomic writes where the platform supports them.
- No backup on Android; no tracking or collected-data declaration on iOS.
- No arbitrary HTML rendering, shell access, code execution or imported active content.
- Release signing is an operator responsibility; signing keys must never enter Git.
