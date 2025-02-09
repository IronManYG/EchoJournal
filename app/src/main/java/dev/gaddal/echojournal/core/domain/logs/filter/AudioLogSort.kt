package dev.gaddal.echojournal.core.domain.logs.filter

/**
 * Example sorting choices for AudioLog.
 */
sealed class AudioLogSort {
    data object DateAscending : AudioLogSort()
    data object DateDescending : AudioLogSort()
    data object TitleAscending : AudioLogSort()
    data object TitleDescending : AudioLogSort()
}