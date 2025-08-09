EchoJournal – Project Guidelines for Contributors

Last updated: 2025-08-09

Scope
- This document captures project-specific details that help advanced contributors build, test, and extend EchoJournal efficiently. It focuses on practical steps and constraints observed in this repository.

1. Build and Configuration
- Toolchain
  - Gradle Wrapper: use the repository’s wrapper (gradlew/gradlew.bat). Do not rely on a locally installed Gradle.
  - Kotlin: 2.0.20 (see README badges).
  - Android Gradle Plugin: inferred from tasks; keep versions consistent with build.gradle.kts/settings.gradle.kts.
  - Java: use the toolchain auto-detected by Gradle (check with: .\gradlew.bat javaToolchains). If you override, prefer a stable LTS (e.g., JDK 17) compatible with your AGP.
- Android SDK
  - Min SDK: 25; Target SDK: 35 (see README badges).
  - You only need Android SDK/emulator for instrumentation tests and running the app on a device/emulator. Pure JVM unit tests do not require the Android SDK.
- Single-module layout
  - App module: :app (all code and tests live here).
  - Room schemas are versioned under app\schemas (Gradle task copyRoomSchemas is present), enabling schema history and migration testing.
- Common Gradle tasks (Windows examples)
  - Sync/resolve: .\gradlew.bat help or .\gradlew.bat projects
  - Assemble all: .\gradlew.bat :app:assemble
  - Build (assemble + tests): .\gradlew.bat :app:build
  - Lint: .\gradlew.bat :app:lint or :app:lintDebug
  - Unit tests (Debug): .\gradlew.bat :app:testDebugUnitTest
  - Instrumentation tests (device/emulator): .\gradlew.bat :app:connectedDebugAndroidTest
- Notes on KSP/Room
  - KSP tasks like :app:kspDebugKotlin run during tests; Room warnings about missing indices can appear (seen for AudioLogTopicEntity.topicId). Address by adding appropriate @Index on foreign key columns when touching DB entities.

2. Testing
- Test types
  - JVM unit tests (fast, no Android runtime). Location: app\src\test\java\...
  - Instrumentation tests (run on device/emulator). Location: app\src\androidTest\java\...
- Running tests
  - All unit tests (Debug):
    - .\gradlew.bat :app:testDebugUnitTest
  - Filter a specific test/class (supported on unit test tasks):
    - .\gradlew.bat :app:testDebugUnitTest --tests dev.gaddal.echojournal.ExampleUnitTest
    - .\gradlew.bat :app:testDebugUnitTest --tests dev.gaddal.echojournal.ExampleUnitTest.addition_isCorrect
  - All unit tests (Release):
    - .\gradlew.bat :app:testReleaseUnitTest
  - All variants:
    - .\gradlew.bat :app:test
  - Instrumentation tests (requires device/emulator):
    - .\gradlew.bat :app:connectedDebugAndroidTest
- Existing example test (validated)
  - File: app\src\test\java\dev\gaddal\echojournal\ExampleUnitTest.kt
  - Status: Verified passing via .\gradlew.bat :app:testDebugUnitTest --tests dev.gaddal.echojournal.ExampleUnitTest.addition_isCorrect on 2025-08-09 in this environment.
- Adding a new unit test
  - Prefer pure Kotlin tests that do not reference android.* APIs or Compose. Example template:
    - Package under dev.gaddal.echojournal or the feature package you’re testing.
    - Use org.junit.Test and org.junit.Assert.*.
    - Avoid Android dependencies; test business logic (e.g., repository filtering params, small pure functions) or compose-independent view-model logic with fake interfaces.
  - If you need Android classes, move the test to androidTest and use instrumentation (slower; requires device/emulator) or add Robolectric (not currently configured; consider only if necessary).
- Guidelines for writing tests specific to EchoJournal
  - Database (Room): Use in-memory Room or Robolectric only when necessary. Given KSP/Room runs during unit tests, keep DB tests isolated and provide migration coverage if you modify schemas (update app\schemas and add migration tests).
  - Coroutines/Flow: Prefer runTest from kotlinx-coroutines-test for deterministic scheduling when testing flows from repositories or trackers.
  - Audio/tracking: Abstract side-effects via interfaces already present (AudioRecordingTracker, AudioPlaybackTracker). Provide fakes for unit tests and assert state transitions in view models without touching the Android framework.

3. Development Notes (Project-specific)
- Architecture
  - The project follows an MVI-ish approach with ViewModels exposing state via StateFlow and handling actions (e.g., EntriesViewModel.onAction). Keep intents/actions sealed and state immutable; derive filtered lists via dedicated functions rather than mutating source collections in-place.
- DI
  - Koin is used for dependency injection. When adding new services (e.g., repositories or trackers), create appropriate Koin modules under app\src\main\java\dev\gaddal\echojournal\di and wire them in Application startup.
- Data layer
  - Room entities and DAOs live under core\database. If you add/modify entities:
    - Add indices for foreign keys used in relations to avoid KSP/Room warnings and full table scans (e.g., add @Index(value = ["topicId"]) on AudioLogTopicEntity if you extend it).
    - Update schema JSON under app\schemas via the existing Gradle configuration.
- UI (Compose)
  - The design system lives under core\presentation\designsystem. Reuse color/typography components and the existing EchoJournalScaffold. For new components, keep previews and avoid introducing Android framework dependencies in unit tests.
- Navigation
  - Navigation graphs are under core\presentation\ui\navigation and navigation\. Maintain a single source of truth for routes; prefer type-safe arguments.
- Logging
  - Timber is configured. Use Timber.* for debug logs. Avoid println in production code. For tests, you can assert logs indirectly by exposing observable state.
- Audio
  - Recording uses MediaRecorder; playback uses MediaPlayer. Both are lifecycle-sensitive. Tests should not instantiate these; instead, verify interactions through trackers and state.
- Permissions
  - Microphone permission handling is represented via actions like SubmitAudioPermissionInfo. Keep permission UI logic in the UI layer; ViewModels should treat it as state.

4. Example: Create and Run a Simple Test (Validated)
- The repository already contains a simple unit test that demonstrates the flow:
  - Location: app\src\test\java\dev\gaddal\echojournal\ExampleUnitTest.kt
  - Command to run just this test (Windows PowerShell/CMD):
    - .\gradlew.bat :app:testDebugUnitTest --tests dev.gaddal.echojournal.ExampleUnitTest.addition_isCorrect
  - Expected result: BUILD SUCCESSFUL and the test passes.
- To add another quick test (no Android deps):
  - Create: app\src\test\java\dev\gaddal\echojournal\SanityTest.kt
  - Content:
    - package dev.gaddal.echojournal
    - import org.junit.Assert.assertEquals
    - import org.junit.Test
    - class SanityTest { @Test fun math() { assertEquals(9, 3 * 3) } }
  - Run: .\gradlew.bat :app:testDebugUnitTest --tests dev.gaddal.echojournal.SanityTest

5. Troubleshooting
- Unknown --tests option
  - Use the option with a concrete Test task (e.g., :app:testDebugUnitTest). Some aggregate tasks may not support it directly.
- Android SDK issues during unit tests
  - JVM unit tests do not need the SDK. If your test references android.* and fails, move it to androidTest or replace framework calls with abstractions/fakes.
- KSP/Room warnings
  - Add indices to FK columns and update schemas. Warnings do not fail the build by default but can signal performance pitfalls.

6. Conventions and Style
- Kotlin style: idiomatic Kotlin with immutable state and data classes; prefer val over var; use sealed interfaces/classes for actions and events.
- Naming: keep packages aligned with features (journal, record, settings) and layers (core.data, core.domain, core.presentation).
- Testing style: small, fast, deterministic tests; avoid flakes by using test Dispatchers and runTest.

Appendix – Useful Commands (Windows)
- Run all debug unit tests: .\gradlew.bat :app:testDebugUnitTest
- Run a specific test: .\gradlew.bat :app:testDebugUnitTest --tests some.fqn.ClassName.method
- Lint: .\gradlew.bat :app:lintDebug
- Assemble APK (debug): .\gradlew.bat :app:assembleDebug
- Print available tasks: .\gradlew.bat :app:tasks --all
