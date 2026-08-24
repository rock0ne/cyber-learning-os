# Security policy

## Supported version

Only the latest release is supported during the prototype stage.

## Reporting

Please open a GitHub security advisory rather than a public issue for vulnerabilities.
Do not include real learner records, credentials or sensitive course material.

## Current posture

The application is offline-first and contains no server, authentication, analytics or
embedded browser. Release signing material and user data must not be committed.

Android public releases are signed with a dedicated private key stored outside Git and
provided through encrypted GitHub Actions secrets. The repository contains no private
signing material.
