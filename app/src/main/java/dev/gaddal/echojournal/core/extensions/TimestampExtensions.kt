package dev.gaddal.echojournal.core.extensions

import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLog
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

/**
 * Returns "Today" if [LocalDate] is the current day,
 * "Yesterday" if [LocalDate] is one day before the current day,
 * otherwise returns the string representation of the date.
 */
fun LocalDate.formatDisplay(): String {
    val nowInstant = Clock.System.now()
    val currentDate = nowInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    val yesterday = currentDate.minus(1, DateTimeUnit.DAY)

    return when (this) {
        currentDate -> "Today"
        yesterday -> "Yesterday"
        else -> this.toString() // Or a custom format, like DateTimeFormatter.ISO_LOCAL_DATE
    }
}


/**
 * Returns a [LocalDate] object by converting this epoch millisecond [Long].
 * This uses the system's default time zone unless otherwise specified.
 */
val Long.asLocalDate: LocalDate
    get() = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

/**
 * Converts this epoch millisecond [Long] into a [LocalDate].
 *
 * @param timeZone The time zone used to convert the epoch millisecond to local date.
 * @return The [LocalDate] corresponding to this timestamp.
 */
fun Long.toLocalDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate {
    return Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(timeZone)
        .date
}

/**
 * Converts this nullable epoch millisecond [Long] into a [LocalDate].
 * Returns null if the value is null.
 *
 * @param timeZone The time zone used to convert the epoch millisecond to local date.
 * @return The [LocalDate] corresponding to this timestamp, or null if the value is null.
 */
fun Long?.toLocalDateOrNull(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate? {
    return this?.let {
        Instant.fromEpochMilliseconds(it)
            .toLocalDateTime(timeZone)
            .date
    }
}

/**
 * Convenient extension for retrieving the [LocalDate] of when an [AudioLog] was created.
 */
fun AudioLog.toLocalDate(): LocalDate = createdAt.asLocalDate

/**
 * Converts a [LocalDate] to the start-of-day epoch millisecond timestamp.
 *
 * @param timeZone The time zone to be used for conversion.
 * @return The epoch millisecond representing 00:00 (midnight) of this date in [timeZone].
 */
fun LocalDate.toStartOfDayTimestamp(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    return atTime(0, 0)
        .toInstant(timeZone)
        .toEpochMilliseconds()
}

/**
 * Returns the time from this epoch millisecond in 24-hour format (e.g. "17:30").
 *
 * @param timeZone The time zone used to convert the epoch millisecond to local time.
 * @return A formatted string in HH:mm (24-hour) format.
 */
fun Long.to24HourTimeString(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val localDateTime = Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)
    val hour = localDateTime.hour
    val minute = localDateTime.minute
    return "%02d:%02d".format(hour, minute)
}

/**
 * Returns the time from this epoch millisecond in 12-hour format with AM/PM (e.g. "5:30 PM").
 *
 * @param timeZone The time zone used to convert the epoch millisecond to local time.
 * @return A formatted string in h:mm AM/PM format.
 */
fun Long.to12HourTimeString(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val localDateTime = Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)
    val hour = localDateTime.hour
    val minute = localDateTime.minute
    val amPm = if (hour < 12) "AM" else "PM"

    // Adjust hour for 12-hour clock display.
    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    return "%d:%02d %s".format(hour12, minute, amPm)
}

/**
 * Formats a [Duration] as a string in the format "mm:ss".
 *
 * @return A formatted string representing the duration in minutes and seconds.
 */
fun Duration.formatAsMmSs(): String {
    val totalSec = inWholeSeconds
    val minutes = totalSec / 60
    val seconds = totalSec % 60
    return String.format("%02d:%02d", minutes, seconds) // Todo: improve this by add locale
}

