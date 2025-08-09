# EchoJournal – Improvement Plan (Inferred)

Note on source: The file docs/requirements.md was not found in the repository. This plan is inferred from the available materials: docs/tasks.md, README.md, and the project guidelines provided in the request. It organizes improvements by theme, states the current goal/constraint context, issues/risk, proposed actions, and rationale.

## Executive Summary

EchoJournal aims to deliver an audio journaling app with a clean MVI-ish architecture, robust Room-backed persistence, reliable audio recording/playback, and a testable, modular codebase. Key constraints include Kotlin 2.0.20, Android minSdk 25/targetSdk 35, single-module app structure, Koin for DI, and a preference for fast JVM unit tests. The plan focuses on: build health and consistency, data/indexing/migrations, MVI conventions and error/event flows, navigation route centralization, UI accessibility and state placeholders, audio robustness, filtering end-to-end, comprehensive unit tests, logging/observability, performance, CI/tooling, and developer documentation.

## Goals and Constraints (Extracted)

- Tooling and versions
  - Kotlin 2.0.20; use Gradle wrapper only; targetSdk 35, minSdk 25.
  - Compose BoM 2025.02.00 target; centralize dependencies via version catalog.
- Architecture and testing
  - MVI-ish: immutable State, sealed Actions/Events, side-effects via ViewModels.
  - Prefer JVM unit tests; avoid android.* in unit tests; use fakes and runTest.
- Data layer
  - Room schemas versioned in app/schemas and exported; indices for frequently queried fields and foreign keys; migrations tested.
- DI and navigation
  - Koin modules per layer; single source of truth for routes; type-safe args; deep-link readiness.
- Audio
  - MediaRecorder/MediaPlayer wrapped by trackers; avoid framework objects in unit tests; handle focus/interruption.
- Logging
  - Timber configured; standardize debug logging; avoid println.

---

## 1) Build, Configuration, and Dependency Hygiene

- Current state ➜ Likely builds locally; tasks.md requests establishing baseline build checks and centralizing versions.
- Issue ➜ Inconsistent local setups and version drift can cause flaky builds and upgrade pain.
- Action
  - Establish build health commands in README (assemble, lint, unit tests). [Tasks 1–2]
  - Introduce version catalog (libs.versions.toml) or ensure centralization of all versions. [Task 4]
  - Add Detekt plugin integration and (later) CI wiring. [Task 3]
- Rationale ➜ Standardized build and style tooling reduce friction, enforce consistency, and ease upgrades.

## 2) Data & Persistence (Room)

- Current state ➜ Entities/DAOs exist; schemas exported under app/schemas. Some indices and FK behaviors may be missing.
- Issues
  - Potential full table scans on join table; incomplete FK cascade semantics. [Tasks 5–7]
  - Schema history needs regeneration and migration testing scaffold. [Task 8]
  - DAO query behavior lacks JVM test coverage. [Tasks 9–10]
- Actions
  - Add @Index on AudioLogTopicEntity(audioLogId, topicId). [Task 5]
  - Ensure FK onDelete = CASCADE for AudioLogTopicEntity, as appropriate. [Task 6]
  - Add indices for frequently filtered/sorted fields (e.g., createdAt, archived). [Task 7]
  - Ensure Room plugin task :app:copyRoomSchemas is used to copy/export schemas; optional alias :
    app:regenerateRoomSchemas depends on copyRoomSchemas; scaffold a migration test. [Task 8]
  - Add DAO and repository tests using in-memory Room where feasible. [Tasks 9–10]
- Rationale ➜ Correct indexing and FK semantics deliver performance and data integrity. Schema discipline enables safe evolution.

## 3) Domain, MVI Conventions, and Error/Event Flows

- Current state ➜ MVI-ish approach with ViewModels exposing StateFlow.
- Issues
  - Conventions not explicitly documented; filter params incomplete mapping; error propagation/Event handling may be missing. [Tasks 11–15]
- Actions
  - Document MVI conventions for feature folders. [Task 11]
  - Extract filtering parameters (query, date range, sort) into UI state; ensure mapping to FilterAudioLogParams is comprehensive. [Task 12]
  - Add Result/DataError error surfaces in repositories and propagate to UI via Events. [Task 13]
  - Introduce one-shot UI event channel wrapper to prevent re-delivery. [Task 14]
  - Verify business logic remains in ViewModels/use cases, not Composables. [Task 15]
- Rationale ➜ Clear conventions improve maintainability; robust error/event handling improves UX and testability.

## 4) Dependency Injection (Koin)

- Current state ➜ Koin used across layers.
- Issues ➜ Possible gaps in registrations and lack of test modules. [Tasks 16–18]
- Actions
  - Audit and partition Koin modules per layer; ensure lifetimes are appropriate. [Task 16]
  - Provide test modules to override repositories/trackers with fakes. [Task 17]
  - Add startup/unit check for Koin definitions (koin-test). [Task 18]
- Rationale ➜ Clean DI configuration enables reliable wiring and easy testing.

## 5) Navigation

- Current state ➜ Navigation graphs under core.presentation.ui.navigation and navigation/.
- Issues ➜ Routes may be duplicated; not all arguments are type-safe. [Tasks 19–21]
- Actions
  - Centralize route definitions into a single source with typed routes. [Task 19]
  - Ensure type-safe arguments and add placeholder deep links for share/open flows. [Task 20]
  - Add unit-level navigation tests (where possible) for Entries actions/callbacks. [Task 21]
- Rationale ➜ Single source of truth prevents drift; typed routes prevent runtime errors; tests ensure intent correctness.

## 6) UI/Compose & Design System

- Current state ➜ Design system present; previews exist for some components.
- Issues ➜ Accessibility checks, expanded previews, and explicit UI placeholders may be missing. [Tasks 22–26]
- Actions
  - Add accessibility validations: content descriptions, touch targets, color contrast checks. [Task 22]
  - Expand Compose previews for key components across states. [Task 23]
  - Add loading/empty/error placeholders in Entries/Entry screens. [Task 24]
  - Extract duplicate gradients/colors; confirm dynamic theming readiness; light/dark toggles. [Task 25]
  - Ensure no Compose unit test pulls android.* frameworks; use interfaces/fakes. [Task 26]
- Rationale ➜ Better UX/accessibility and preview coverage accelerate design feedback; isolation improves test reliability.

## 7) Audio Recording/Playback Robustness

- Current state ➜ Trackers abstract MediaRecorder/MediaPlayer.
- Issues ➜ Hard-coded filenames, path/space/permission checks, interruptions/focus handling, mutual exclusivity, and playback position persistence. [Tasks 27–31]
- Actions
  - Introduce FileNameProvider with timestamp/UUID; document naming strategy. [Task 27]
  - Validate storage paths and free space before recording via StoragePathProvider; preflight permissions. [Task 28]
  - Define ViewModel events/states for interruptions/errors; snackbars/dialogs. [Task 29]
  - Optionally persist last playback position per log and restore. [Task 30]
  - Enforce mutual exclusivity between recording and playback (auto-stop). [Task 31]
- Rationale ➜ Prevents user data loss and broken experiences; increases resilience to real-world conditions.

## 8) Filtering and List Behavior

- Current state ➜ Filtering exists but may be incomplete.
- Issues ➜ End-to-end implementation for query/date/sort and tests needed; empty-state guidance missing. [Tasks 32–34]
- Actions
  - Implement full filter pipeline: State -> FilterAudioLogParams -> FilterAudioLogImpl -> UI; add sort toggles. [Task 32]
  - Add unit tests for combined filters and sorting. [Task 33]
  - Add empty-state hints with quick “clear filters” actions. [Task 34]
- Rationale ➜ Robust, testable filtering improves findability and UX for large datasets.

## 9) Testing Strategy

- Current state ➜ Example tests present.
- Issues ➜ Gaps in ViewModel, repository, DAO, and filter implementation coverage; need fakes. [Tasks 35–38]
- Actions
  - Add EntriesViewModel unit tests for actions: mood/topic selection, recording lifecycle, playback, filtering. [Task 35]
  - Provide fakes for AudioRecordingTracker/AudioPlaybackTracker for deterministic tests using runTest. [Task 36]
  - Create sample repository fakes producing deterministic AudioLogWithTopics for UI/ViewModel tests. [Task 37]
  - Keep Android-dependent tests in androidTest; consider Robolectric only if needed. [Task 38]
- Rationale ➜ Deterministic, fast tests raise confidence and reduce regressions.

## 10) Logging and Observability

- Current state ➜ Timber configured.
- Issues ➜ Ensure initialization and standardized usage; add debug-only logs at key flows. [Tasks 39–40]
- Actions
  - Verify Timber initialization; standardize tags/levels; remove println. [Task 39]
  - Add debug logs around recording/playback transitions and filter computations. [Task 40]
- Rationale ➜ Actionable logs ease diagnosis during development and QA.

## 11) Performance and Stability

- Current state ➜ Potential recomputation in flows and list rendering concerns for large datasets.
- Issues ➜ Unnecessary recomputations, heavy work on main dispatcher, and LazyColumn scaling. [Tasks 41–43]
- Actions
  - Guard filter recomputation; use distinctUntilChanged where suitable. [Task 41]
  - Move heavy filtering to background dispatcher; add basic measurement in debug. [Task 42]
  - Optimize LazyColumn: stable keys, item reuse where possible. [Task 43]
- Rationale ➜ Keeps UI responsive and scalable as data grows.

## 12) CI, Release Hygiene, and Tooling

- Current state ➜ CI not guaranteed; Room schema copy needs verification; Proguard rules may need attention.
- Issues ➜ Missing CI and static analysis; schema export task; R8/keep rules. [Tasks 44–47]
- Actions
  - Add minimal CI (GitHub Actions) running build, lint, and unit tests with Gradle cache. [Task 44]
  - Wire static analysis in CI (Detekt). [Task 45]
  - Verify schema export/copy task in Gradle; ensure JSONs committed. [Task 46]
  - Review Proguard/R8 rules for Timber/Room to avoid stripping critical classes. [Task 47]
- Rationale ➜ Automation prevents regressions and ensures consistent quality on PRs.

## 13) Documentation & Developer Experience

- Current state ➜ README exists; guidelines are available.
- Issues ➜ Missing architecture diagram, feature-development guide, and review conventions. [Tasks 48–50]
- Actions
  - Expand README with architecture overview (presentation, domain, data; trackers; Room). [Task 48]
  - Add “How to add a new feature” guide covering ViewModel (State/Action/Event), Koin wiring, navigation, and tests. [Task 49]
  - Add CODEOWNERS or review guidelines to maintain MVI/design system conventions. [Task 50]
- Rationale ➜ Better onboarding and consistency for contributors.

---

## Risks, Constraints, and Mitigations

- Android SDK and AGP compatibility: ensure toolchain matches Kotlin 2.0.20 and targetSdk 35; verify Gradle sync and KSP tasks locally.
- Room migrations: changes to indices/FKs require schema regeneration and migration consideration; mitigate via migration scaffold and tests.
- Audio APIs: device-specific quirks (focus/interruptions) — mitigate with robust state/event handling and clear UI feedback.
- Test isolation: avoid android.* in unit tests; use fakes; move framework-dependent tests to androidTest.

## Milestones and Phased Roadmap

- Phase 1: Build & Lint Baseline, Version Catalog, Logging
  - Tasks 1–4, 39–40.
- Phase 2: Data/Room Integrity & Tests
  - Tasks 5–10, 46.
- Phase 3: MVI Conventions, Error/Event Flows, Filtering E2E
  - Tasks 11–15, 32–34.
- Phase 4: Audio Robustness
  - Tasks 27–31.
- Phase 5: UI/Compose, Accessibility, Navigation
  - Tasks 19–26, 21.
- Phase 6: Testing Expansion
  - Tasks 35–38.
- Phase 7: Performance & Stability
  - Tasks 41–43.
- Phase 8: CI & Release Hygiene
  - Tasks 44–47.
- Phase 9: Documentation & DX
  - Tasks 48–50.

## Acceptance Criteria & Success Metrics

- Build health: :app:build, :app:lintDebug, and :app:testDebugUnitTest pass locally and in CI.
- Data layer: No Room warnings about missing indices; migration tests pass; DAO/repository tests cover critical paths.
- Architecture: Documented MVI conventions; ViewModels expose complete filter state; one-shot events implemented.
- Navigation: Single source of truth for routes; type-safe args; navigation tests green.
- UI/UX: Accessibility checks in place; previews cover major states; explicit loading/empty/error states visible.
- Audio: Resilient to interruptions; clear error events; mutual exclusivity enforced; optional resume playback position works.
- Performance: Reduced recomputation; background filtering; large list render remains responsive.
- Logging: Timber initialized; debug logs around audio/filters; no println in production.
- Documentation: README updated with architecture and feature guide; CODEOWNERS/review guidelines (optional) added.
