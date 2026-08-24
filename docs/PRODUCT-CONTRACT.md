# V1 Product Contract

## Purpose

Cyber Learning OS is a personal learning enforcer, not a content library. It helps a
learner build and retain technical judgement while working at high information volume.

## Invariants

1. Consuming content never marks a topic learned.
2. The learner states purpose and observable capability before starting.
3. Prime occurs before detailed acquisition.
4. Retrieval evidence is learner-authored and recorded without source material open.
5. Application requires a lab, scenario, investigation, decision or equivalent artifact.
6. Important learning is explained to analyst, technical-leader and executive audiences.
7. AI, when added, must follow an attempt and cannot overwrite learner evidence.
8. Review scheduling responds to demonstrated performance, not confidence clicks alone.
9. Learning Debt may decrease only when missing evidence is produced or a review is completed.
10. V1 is local-only and collects no analytics or personal data remotely.

## V1 acceptance loop

```text
Create topic
  -> require purpose + capability
  -> capture priming gist
  -> capture selective learning notes
  -> capture relational connections
  -> reconstruct from memory
  -> record practical application
  -> explain to three audiences
  -> compare reasoning and record feedback
  -> schedule adaptive review
```

The app blocks progression when the active stage has no evidence. It does not assess
the truth quality of free text in V1; that is an explicit limitation rather than a
claim of verified competency.

## Learning Debt

Debt is an action signal, not a grade. Each topic accrues one point while retrieval is
missing, one while application is missing and one while three-audience explanation is
missing. An overdue scheduled review adds two points. This deliberately makes passive
acquisition visible without pretending the score measures workplace competence.

## Review policy

The learner rates a retrieval/application attempt:

| Rating | Next interval |
|---|---|
| Again | 1 day |
| Hard | at least 2 days; retain the current interval when longer |
| Good | at least 3 days; double the current interval |
| Strong | at least 7 days; triple the current interval |

## Source and credit

The orientation, priming, connection, performance and retention principles are inspired
by Dr Justin Sung's video
[My Exact 14-Step Guide To Learn Anything Faster](https://youtu.be/CQQTwvDb5xg).
The software does not reproduce a transcript or claim authorship of that framework.

## Deferred deliberately

- AI coaching and AI-withholding policy enforcement
- Knowledge-graph visualisation
- Confusion Compass as a dedicated entity
- Cross-device encrypted sync
- Import/export and backup
- Scenario generation
- Account identity and subscriptions
- Integration with VeriTacta

These are roadmap items, not implied V1 capabilities.
