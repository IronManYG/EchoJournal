# EchoJournal – Improvement Tasks Checklist

Note: Each actionable task is enumerated and prefixed with a checkbox placeholder. Execute roughly in order for best impact.

- [x] 1. Establish a baseline Gradle build health check pipeline (locally): verify :app:build, :app:lintDebug, and :app:testDebugUnitTest all pass; document commands in README’s contributor section.
- [x] 2. Add a simple CONTRIBUTING section (or link) to README referencing .\gradlew.bat usage, Java toolchain, and test commands defined in .junie/guidelines.md.
- [x] 
    3. Introduce a consistent Kotlin code style/lints: wire ktlint or detekt(preferred) via Gradle
       and a pre-commit hook; add tasks to CI (if CI is added later).

### Static analysis – Detekt

The project uses Detekt with the detekt-formatting ruleset (ktlint-backed) and auto-correct enabled.

Common commands (Windows PowerShell/CMD):

- Analyze with auto-correct:
    - .\gradlew.bat detekt
    - Or explicitly: .\gradlew.bat detekt --auto-correct
- Create/update baseline (grandfather existing issues):
    - .\gradlew.bat detektBaseline
- Generate a full default config (for reference/diff):
    - .\gradlew.bat detektGenerateConfig

Notes:

- Detekt is applied to all modules via the root build.gradle.kts.
- Formatting fixes are applied automatically due to auto-correct being true in Gradle.
- Reports are generated under build\reports\detekt\ (HTML, XML, SARIF, MD, per-module defaults).
- Config is at config\detekt\detekt.yml and builds upon Detekt's default config.
- The ktlint Gradle plugin has been removed. The project uses a single tool: Detekt with the
  detekt-formatting ruleset (ktlint-backed).
- Compose-specific rules are enabled via io.nlopez.compose.rules:detekt (version pinned in Gradle).
- Generic FunctionNaming is disabled (naming.FunctionNaming.active=false) in favor of Compose naming checks.
- [x] 4. Create a libs.versions.toml or centralize dependency versions (if version catalog already used, ensure all versions are centralized) to ease upgrades and maintain consistency with Kotlin 2.0.20 and Compose BoM 2025.02.00.

Data layer (Room) and persistence
- [x] 5. Add @Index annotations to Room join table AudioLogTopicEntity for columns audioLogId and topicId to avoid full table scans and align with KSP/Room guidance.
- [x] 6. Ensure foreign keys in AudioLogTopicEntity specify onDelete = CASCADE where appropriate so orphan rows are not left behind when AudioLog or Topic is deleted.
- [x] 7. Verify that all frequently queried fields (e.g., AudioLogEntity.createdAt, archived) have indices when used in WHERE/ORDER BY clauses; add @Index where beneficial.
- [x] 
    8. Confirm Room schema JSONs are up-to-date under app/schemas and add a migration test scaffold;
       rely on the Room plugin task :app:copyRoomSchemas (optional alias :app:regenerateRoomSchemas
       dependsOn copyRoomSchemas).
- [ ] 9. Add DAO query coverage tests (pure JVM with in-memory Room if feasible or Robolectric if needed later) for getAudioLogsWithTopics() and topic retrieval.
- [ ] 10. Add repository tests for OfflineFirstAudioLogRepository and OfflineFirstTopicRepository using fakes/in-memory DB to validate mapping and filtering behavior.

Domain and architecture

- [x] 
    11. Review and document MVI conventions for feature folders (journal, record, settings): ensure
        State is immutable, Actions are sealed, and side-effects are funneled via ViewModel.
- [x] 12. Extract filtering parameters (query, date range, sort) into UI state where relevant and ensure EntriesState -> FilterAudioLogParams mapping is comprehensive (currently query/date unset).
- [x] 
    13. Ensure error handling paths exist in repositories (Result<DataError>): propagate errors to
        ViewModel as UI Events; add a minimal EntriesEvent sealed type and observer in UI.
- [x] 
    14. Introduce a one-shot UI event channel abstraction (already Channel-based) with a thin
        wrapper to avoid event re-delivery after configuration changes.
- [ ] 15. Verify that no business logic leaks into Composables; keep UI logic in Composables and data/state/side-effects in ViewModels and use cases.

DI (Koin)
- [ ] 16. Audit Koin modules: ensure all repositories, trackers, and use-cases are registered with proper lifetimes; create a dedicated module per layer (data, domain, presentation wiring) if needed.
- [ ] 17. Provide test modules for unit tests to override repositories/trackers with fakes, enabling fast ViewModel tests.
- [ ] 18. Add a lightweight startup check (optional) to verify Koin definitions on app start or as a unit test using koin-test.

Navigation
- [ ] 19. Centralize route definitions (MainScreen, JournalScreen) in a single source of truth and ensure all usages go through these typed routes.
- [ ] 20. Add type-safe arguments for all routes that need them (EntryDetails already typed); add deep link placeholders for future share/open flows.
- [ ] 21. Add navigation tests (unit, where possible) asserting that actions from EntriesViewModel or callbacks in EntriesScreenRoot trigger the expected routes.

UI/Compose & design system
- [x] 22. Add accessibility checks: content descriptions for interactive elements, minimum touch targets, and color contrast validations in design components.
- [ ] 23. Expand Compose previews for EntryCard, EntryList, and the recording bottom sheet across different states (empty, loading, recording, playing) to aid design reviews.
- [ ] 24. Introduce UI state placeholders for loading/empty/error in EntriesScreen and Entry screen; show appropriate UI instead of silently updating lists.
- [ ] 25. Extract duplicate gradient/color usages into the design system if found; confirm dynamic theming readiness or plan for light/dark theme toggle.
- [ ] 26. Ensure no Compose code references Android framework classes in unit tests; factor such dependencies behind interfaces.

Audio recording/playback robustness
- [x] 27. Specify and document the audio file naming strategy (avoid hard-coded "my_recording.mp4"); inject a FileNameProvider with timestamp/UUID.
- [ ] 28. Validate storage paths via StoragePathProvider; add checks for available space and permissions preflight before starting a recording.
- [ ] 29. Handle tracker errors and interruptions (phone call, focus loss): define ViewModel event(s) and state for error dialogs or snackbars.
- [ ] 30. Persist last known playback position per log (optional) and restore when reopening the app.
- [x] 31. Ensure mutual exclusivity between recording and playback (auto-stop playback when recording starts and vice versa) to avoid audio focus conflicts.

Filtering and list behavior
- [ ] 32. Implement search query and date range filters end-to-end (state -> FilterAudioLogParams -> FilterAudioLogImpl -> UI). Add sort order toggle (date asc/desc, duration, mood).
- [ ] 33. Add unit tests for FilterAudioLogImpl covering combined filters (mood + topics + date + query) and sorting.
- [ ] 34. Add empty-state hints when filters produce no results, with quick actions to clear filters.

Testing
- [ ] 35. Add unit tests for EntriesViewModel: action handling for mood/topic selection, recording lifecycle, playback control, and filtering invocation.
- [ ] 36. Add fakes for AudioRecordingTracker and AudioPlaybackTracker to deterministically drive state in ViewModel tests (using runTest).
- [ ] 37. Add sample repository fakes generating deterministic AudioLogWithTopics data for UI/ViewModel tests.
- [ ] 38. Keep Android-dependent tests in androidTest only; consider Robolectric only if necessary (not configured by default).

Logging/observability
- [x] 39. Ensure Timber is initialized in Application; standardize log tags and levels; replace println with Timber in production code if any.
- [x] 40. Add debug-only logging around recording/playback start/stop/pause/resume and filter computations to help diagnose issues.

Performance and stability
- [ ] 41. Audit flows for unnecessary recomputation: ensure filterEntries() is called only when relevant state changes; consider distinctUntilChanged on repositories.
- [ ] 42. Offload heavy filtering to a background dispatcher if datasets grow; measure with simple benchmarks (microbenchmark or runtime timers in debug).
- [ ] 43. Evaluate list rendering performance with large datasets; consider LazyColumn optimizations and stable keys.

Build/CI and release hygiene
- [ ] 44. Add a minimal CI workflow (GitHub Actions or other) to run :app:build, :app:lintDebug, and :app:testDebugUnitTest on PRs; use Gradle cache and Java toolchain matrix if needed.
- [ ] 
    45. Introduce static analysis in CI (Detekt) and report annotations.
- [x] 
    46. Confirm Room plugin task :app:copyRoomSchemas is used to export schemas; avoid custom
        KSP-wired tasks. Document commands and (optional) :app:regenerateRoomSchemas alias that
        depends on copyRoomSchemas; use in CI if desired (not wired to build).
- [ ] 47. Add Proguard/R8 rules if minify is planned later; ensure Timber and Room keep rules are adequate for release builds.

Documentation & developer experience
- [ ] 48. Expand README with a short architecture diagram (layers: presentation, domain, data; trackers; Room) and how features are organized.
- [ ] 49. Add a brief "How to add a new feature" guide: create ViewModel with State/Action/Event, wire Koin, update navigation, write unit tests.
- [ ] 50. Add codeowners or review guidelines (optional) for keeping MVI and design system conventions consistent.
