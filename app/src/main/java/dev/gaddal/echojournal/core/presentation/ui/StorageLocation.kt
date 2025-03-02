package dev.gaddal.echojournal.core.presentation.ui

/**
 * Enum class to define the storage location for downloaded files.
 *
 * This enum is used to specify where a downloaded file should be stored
 * within the app's storage. It provides three options:
 * - CACHE: For temporary storage in the app's cache directory.
 * - INTERNAL: For more permanent storage in the app's internal storage directory.
 * - EXTERNAL: For storage in the app's external storage directory, if available.
 *
 * Usage of these storage locations should be based on the intended lifespan,
 * importance, and size of the downloaded files.
 */
enum class StorageLocation {
    /** Indicates that the file should be stored in the app's cache directory */
    CACHE,

    /** Indicates that the file should be stored in the app's internal storage directory */
    INTERNAL,

    /** Indicates that the file should be stored in the app's external storage directory, if available */
    EXTERNAL
}