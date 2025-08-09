package dev.gaddal.echojournal.core.presentation.ui.events

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Thin wrapper around Channel to expose one-shot UI events as a Flow,
 * avoiding re-delivery after configuration changes.
 */
class UiEventChannel<E> private constructor(
    private val channel: Channel<E>
) {
    val flow: Flow<E> = channel.receiveAsFlow()

    suspend fun emit(event: E) {
        channel.send(event)
    }

    fun tryEmit(event: E): Boolean = channel.trySend(event).isSuccess

    companion object {
        /**
         * Buffered channel suitable for most UI event streams.
         */
        fun <E> buffered(capacity: Int = Channel.BUFFERED): UiEventChannel<E> =
            UiEventChannel(Channel(capacity))

        /**
         * Rendezvous (no buffer) channel for strict handoff semantics.
         */
        fun <E> rendezvous(): UiEventChannel<E> = UiEventChannel(Channel())
    }
}
