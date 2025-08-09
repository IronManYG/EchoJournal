package dev.gaddal.echojournal.core.extensions

import dev.gaddal.echojournal.R
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLog
import dev.gaddal.echojournal.core.presentation.ui.UiText
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
 * Returns a UiText.StringResource representing "Today" if [LocalDate] is the current day,
 * "Yesterday" if [LocalDate] is one day before the current day,
 * otherwise returns a formatted date string resource.
 */
fun LocalDate.formatDisplay(): UiText {
    val nowInstant = Clock.System.now()
    val currentDate = nowInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    val yesterday = currentDate.minus(1, DateTimeUnit.DAY)

    return when (this) {
        currentDate -> UiText.StringResource(R.string.today)
        yesterday -> UiText.StringResource(R.string.yesterday)
        else -> {
            // Pass date components separately to allow different formatting in different locales
            UiText.StringResource(
                R.string.date_format,
                arrayOf(this.dayOfMonth, this.monthNumber, this.year)
            )
        }
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
fun Long.to24HourTimeString(timeZone: TimeZone = TimeZone.currentSystemDefault()): UiText {
    val localDateTime = Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)
    val hour = localDateTime.hour
    val minute = localDateTime.minute
    return UiText.StringResource(R.string.time_24_hour_format, arrayOf(hour, minute))
}

/**
 * Returns the time from this epoch millisecond in 12-hour format with AM/PM (e.g. "5:30 PM").
 *
 * @param timeZone The time zone used to convert the epoch millisecond to local time.
 * @return A formatted string in h:mm AM/PM format.
 */
fun Long.to12HourTimeString(timeZone: TimeZone = TimeZone.currentSystemDefault()): UiText {
    val localDateTime = Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)
    val hour = localDateTime.hour
    val minute = localDateTime.minute

    val isAm = hour < 12
    val formatResId = if (isAm) R.string.time_12_hour_format_am else R.string.time_12_hour_format_pm

    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    return UiText.StringResource(formatResId, arrayOf(hour12, minute))
}

/**
 * Formats a [Duration] as a string in the format "mm:ss".
 *
 * @return A formatted string representing the duration in minutes and seconds.
 */
fun Duration.formatAsMmSs(): UiText {
    val totalSec = inWholeSeconds
    val hours = totalSec / 3600
    val minutes = (totalSec % 3600) / 60
    val seconds = totalSec % 60

    return if (hours > 0) {
        UiText.StringResource(R.string.duration_format_with_hours, arrayOf(hours, minutes, seconds))
    } else {
        UiText.StringResource(R.string.duration_format, arrayOf(minutes, seconds))
    }
}
