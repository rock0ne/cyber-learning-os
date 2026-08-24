# Contributing

Contributions are welcome when they preserve the product invariants in
`docs/PRODUCT-CONTRACT.md` and maintain equivalent behaviour across Android, iOS and
Windows.

Before submitting a pull request:

1. Add or update policy tests on every affected platform.
2. Run Android test/lint/build, Swift package tests and Windows tests where available.
3. Do not add telemetry, network permissions or AI assistance without a reviewed privacy
   and learning-integrity decision.
4. Do not remove or obscure the method attribution.
