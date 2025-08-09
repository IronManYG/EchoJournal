package dev.gaddal.echojournal.core.domain.util

/**
 * A sealed interface that categorizes different types of data errors (network, local).
 */
sealed interface DataError : Error {
    /**
     * Enumerates various network-related errors that can occur.
     */
    enum class Network : DataError {
        REQUEST_TIMEOUT, // The request timed out before a response was received.
        UNAUTHORIZED, // Unauthorized access due to invalid credentials.
        CONFLICT, // Resource conflict (e.g., editing a resource that another user modified).
        TOO_MANY_REQUESTS, // The client is sending too many requests in a short period.
        NO_INTERNET, // Internet connectivity issues.
        PAYLOAD_TOO_LARGE, // The request payload exceeds the server's limit.
        SERVER_ERROR, // General server-side error (e.g., HTTP 500).
        SERIALIZATION, // Errors during data serialization or deserialization.
        UNKNOWN // Catch-all for unexpected or unclassified errors.
    }

    /**
     * Enumerates common errors that can occur on the client side.
     */
    enum class Local : DataError {
        DISK_FULL, // The disk is full, preventing data from being written.
        PERMISSION_DENIED, // Lack of necessary permissions for an operation.
        FILE_NOT_FOUND, // The specified file or directory cannot be found.
        READ_WRITE_FAILURE, // Errors in reading or writing data.
        OUT_OF_MEMORY, // The application ran out of memory.
        CORRUPT_DATA, // Data in storage is corrupted or unreadable.
        RESOURCE_BUSY, // The resource is currently in use or locked.
        UNKNOWN // Catch-all for unexpected or unclassified errors.
    }
}
