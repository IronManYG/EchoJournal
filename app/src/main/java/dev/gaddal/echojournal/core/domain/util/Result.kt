package dev.gaddal.echojournal.core.domain.util

/**
 * A sealed interface representing the result of an operation that can either succeed with data
 * or fail with an error.
 *
 * @param D The type of data on a successful outcome.
 * @param E The error type that should extend the [Error] interface.
 */
sealed interface Result<out D, out E : Error> {
    /**
     * Indicates a successful result with [data] of type [D].
     */
    data class Success<out D>(val data: D) : Result<D, Nothing>

    /**
     * Indicates an error result containing an [error] of type [E].
     */
    data class Error<out E : dev.gaddal.echojournal.core.domain.util.Error>(val error: E) : Result<Nothing, E>
}

/**
 * Maps the current [Result] of type [T, E] into a new [Result] with a transformed data type [R].
 *
 * @param map A mapping function that will be applied to the data in case of success.
 * @return A [Result] containing either the mapped data or the original error.
 */
inline fun <T, E : Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Error -> Result.Error(error) // Return the same error as is.
        is Result.Success -> Result.Success(map(data)) // Apply the mapping function to the data.
    }
}

/**
 * Transforms the current [Result] into an [EmptyResult] by discarding any successful data.
 *
 * @return An [EmptyResult] that either contains an empty [Unit] or the same error.
 */
fun <T, E : Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map { }
}

/**
 * An alias for a [Result] that does not return any data on success.
 *
 * @param E The type of error that may occur.
 */
typealias EmptyResult<E> = Result<Unit, E>
