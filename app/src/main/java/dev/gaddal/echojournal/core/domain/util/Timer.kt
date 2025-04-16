package dev.gaddal.echojournal.core.domain.util

import dev.gaddal.echojournal.R
import dev.gaddal.echojournal.core.presentation.ui.UiText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object Timer {

    fun timeAndEmit(): Flow<Duration> {
        return flow {
            var lastEmitTime = System.currentTimeMillis()
            while (true) {
                delay(200L)
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - lastEmitTime
                emit(elapsedTime.milliseconds)
                lastEmitTime = currentTime
            }
        }
    }
}

/**
 * Converts a [Duration] to a formatted string resource.
 *
 * @return A [UiText] representing the formatted duration.
 */
fun Duration.formatted(): UiText {
    val totalSeconds = inWholeSeconds
    val hours = totalSeconds / (60 * 60)
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return UiText.StringResource(
        R.string.duration_format_with_hours,
        arrayOf(hours, minutes, seconds)
    )
}