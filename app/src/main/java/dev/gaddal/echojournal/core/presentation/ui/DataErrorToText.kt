package dev.gaddal.echojournal.core.presentation.ui

import dev.gaddal.echojournal.R
import dev.gaddal.echojournal.core.domain.util.DataError

/**
 * Converts a [DataError] to a [UiText] instance that contains a user-friendly error message.
 * The method leverages string resources to deliver localized error messages.
 *
 * @return A [UiText] instance with an appropriate error message based on the given [DataError].
 */
fun DataError.asUiText(): UiText {
    return when (this) {
        DataError.Local.DISK_FULL -> UiText.StringResource(
            R.string.error_disk_full
        )

        DataError.Local.PERMISSION_DENIED -> UiText.StringResource(
            R.string.error_permission_denied
        )

        DataError.Local.FILE_NOT_FOUND -> UiText.StringResource(
            R.string.error_file_not_found
        )

        DataError.Local.READ_WRITE_FAILURE -> UiText.StringResource(
            R.string.error_read_write_failure
        )

        DataError.Local.OUT_OF_MEMORY -> UiText.StringResource(
            R.string.error_out_of_memory
        )

        DataError.Local.CORRUPT_DATA -> UiText.StringResource(
            R.string.error_corrupt_data
        )

        DataError.Local.RESOURCE_BUSY -> UiText.StringResource(
            R.string.error_resource_busy
        )

        DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(
            R.string.error_request_timeout
        )

        DataError.Network.TOO_MANY_REQUESTS -> UiText.StringResource(
            R.string.error_too_many_requests
        )

        DataError.Network.NO_INTERNET -> UiText.StringResource(
            R.string.error_no_internet
        )

        DataError.Network.PAYLOAD_TOO_LARGE -> UiText.StringResource(
            R.string.error_payload_too_large
        )

        DataError.Network.SERVER_ERROR -> UiText.StringResource(
            R.string.error_server_error
        )

        DataError.Network.SERIALIZATION -> UiText.StringResource(
            R.string.error_serialization
        )

        else -> UiText.StringResource(R.string.error_unknown)
    }
}
