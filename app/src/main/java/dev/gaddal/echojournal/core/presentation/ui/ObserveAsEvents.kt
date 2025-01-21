package dev.gaddal.echojournal.core.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Observes a Flow of one-time events and triggers the [onEvent] callback each time an event is emitted.
 *
 * The function uses [LaunchedEffect] to handle lifecycle changes, and only observes the [flow] while
 * the [Lifecycle] is in the STARTED state. This ensures events are collected only when the UI is active.
 *
 * @param flow The [Flow] emitting events to be observed.
 * @param key1 An optional key used to control recomposition.
 * @param key2 An optional key used to control recomposition.
 * @param onEvent A callback function invoked with each event emitted from the [flow].
 */
@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit
) {
    // Retrieve the current lifecycle owner, which helps manage the lifecycle state.
    val lifecycleOwner = LocalLifecycleOwner.current

    // LaunchedEffect will restart this block whenever the keys change.
    LaunchedEffect(flow, lifecycleOwner.lifecycle, key1, key2) {
        // Observes the flow only when the lifecycle is at least STARTED.
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            // Switch to the main thread to collect events.
            withContext(Dispatchers.Main.immediate) {
                // Collect events from the flow and trigger the callback.
                flow.collect(onEvent)
            }
        }
    }
}
