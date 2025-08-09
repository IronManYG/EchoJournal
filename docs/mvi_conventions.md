# EchoJournal – MVI Conventions for Feature Folders

Last updated: 2025-08-09

Scope

- This document describes the practical MVI-ish conventions used across EchoJournal features (
  journal, record, settings). It complements `.junie/guidelines.md` and `docs/plan.md`.

Core principles

- Unidirectional flow: UI emits Actions -> ViewModel reduces to new State and emits one-shot Events
  for side-effects.
- Immutability: State is a data class with vals; never mutate collections in place. Create new lists
  when toggling selections.
- Sealed intents: Define sealed interfaces/classes for Actions and Events per feature.
- Side-effects in ViewModels: Repository calls, tracker interactions, navigation triggers, and error
  mapping happen in ViewModels/use-cases, not in Composables.
- Testability: Prefer pure functions for mapping and filtering; when testing flows, use `runTest`
  and fakes for repositories/trackers.

Folder shape (per feature)

- State: A single immutable data class representing the screen.
- Action: A sealed interface of user intents (button clicks, text changes, etc.).
- Event: A sealed interface for one-shot UI events (snackbars, toasts, navigation).
- ViewModel: Holds State as `StateFlow`, exposes `events` as a Flow, and handles Actions via
  `onAction`.
- UI: Composables render State and collect Events; they do not contain business logic.

One-shot events

- Use a thin wrapper over Channel to avoid event re-delivery after configuration changes.
- Provided utility: `UiEventChannel<E>` (see `core/presentation/ui/events/UiEventChannel.kt`).
    - Channel-backed, exposes `flow` for collection.
    - Use `emit(event)` from ViewModel; collect in UI with `LaunchedEffect`.

Filtering mapping

- Keep UI filter inputs (query, date range, sort, topics, moods) in State.
- Map to domain params using an extension (e.g., `EntriesState.toFilterParams()`).
- Run filtering in ViewModel on relevant state changes, and prefer lightweight logs during
  development.

Error handling

- Repositories return `Result<*, DataError>`; map errors to `UiText` via `DataError.asUiText()` and
  emit `Event.Error(UiText)`.
- Never show raw exceptions or stack traces to the user.

Navigation

- Keep routes centralized and typed. Emit navigation events from ViewModels and handle them in the
  UI layer.

Do and don’t

- Do: Use `Timber` for debug logs; keep log tags consistent (e.g., `EntriesViewModel`,
  `EntryViewModel`).
- Do: Keep Composables side-effect free beyond collecting flows and dispatching actions.
- Don’t: Reference Android framework classes in unit tests; use fakes.

Example (sketch)

```
sealed interface FeatureAction { data object OnClick : FeatureAction }
sealed interface FeatureEvent { data class Error(val msg: UiText) : FeatureEvent }

data class FeatureState(val loading: Boolean = false)

class FeatureViewModel(...) : ViewModel() {
  private val _state = MutableStateFlow(FeatureState())
  val state = _state.asStateFlow()

  private val uiEvents = UiEventChannel.buffered<FeatureEvent>()
  val events = uiEvents.flow

  fun onAction(action: FeatureAction) {
    when (action) {
      FeatureAction.OnClick -> viewModelScope.launch {
        try { /* ... */ } catch (e: Exception) {
          uiEvents.emit(FeatureEvent.Error(UiText.DynamicString("Oops")))
        }
      }
    }
  }
}
```
