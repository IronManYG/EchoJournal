package dev.gaddal.echojournal.core.presentation.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * A sealed interface representing UI text. It can either be a dynamic string value or a string
 * resource (with optional arguments).
 *
 * This class helps in standardizing text handling across different UI layers.
 * It is useful for:
 * - Displaying static or dynamic text values in composable functions.
 * - Fetching string resources from a `ViewModel` through the provided `asString` functions.
 */
sealed interface UiText {
    /**
     * Represents a text that is provided directly as a string value.
     *
     * @param value The string value that is to be displayed.
     */
    data class DynamicString(val value: String) : UiText

    /**
     * Represents a text that is provided as a string resource.
     *
     * @param id The resource ID of the string.
     * @param args Optional arguments to format the string resource.
     */
    class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = arrayOf()
    ) : UiText

    /**
     * Retrieves the string representation of this [UiText] instance, intended for use in
     * composable functions.
     *
     * @return The string value either from the dynamic string or formatted string resource.
     */
    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(id = id, *args)
        }
    }

    /**
     * Retrieves the string representation of this [UiText] instance using the provided [context].
     * This is helpful for non-composable contexts, like `ViewModel`s or activities.
     *
     * @param context The Android [Context] to be used for string resource retrieval.
     * @return The string value either from the dynamic string or formatted string resource.
     */
    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(id, *args)
        }
    }
}
