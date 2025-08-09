package dev.gaddal.echojournal.core.presentation.ui

import android.content.Context
import android.os.Environment

/**
 * Provides appropriate file system paths for different storage locations.
 *
 * This class is responsible for determining the correct file system path
 * based on the specified [StorageLocation]. It encapsulates the logic for
 * accessing different storage areas of the Android device, making it easier
 * to manage file storage across various parts of the application.
 *
 * @property context The application context, used to access system directories.
 */
class StoragePathProvider(private val context: Context) {

    /**
     * Retrieves the appropriate file system path for the given storage location.
     *
     * @param storageLocation The desired storage location (CACHE, INTERNAL, or EXTERNAL).
     * @return A String representing the absolute path to the specified storage location.
     * @throws IllegalStateException if external storage is not available when EXTERNAL is requested.
     */
    fun getPath(storageLocation: StorageLocation): String {
        return when (storageLocation) {
            StorageLocation.CACHE -> getCachePath()
            StorageLocation.INTERNAL -> getInternalPath()
            StorageLocation.EXTERNAL -> getExternalPath()
        }
    }

    /**
     * Retrieves the path to the app's cache directory.
     *
     * @return The absolute path to the app's cache directory.
     */
    private fun getCachePath(): String = context.cacheDir.absolutePath

    /**
     * Retrieves the path to the app's internal storage directory.
     *
     * @return The absolute path to the app's internal storage directory.
     */
    private fun getInternalPath(): String = context.filesDir.absolutePath

    /**
     * Retrieves the path to the app's external storage directory.
     *
     * This method checks for the availability of external storage and returns the appropriate path.
     * If external storage is not available or not currently accessible, it falls back to internal storage.
     *
     * @return The absolute path to the app's external storage directory, or internal storage if external is unavailable.
     * @throws IllegalStateException if both external and internal storage are unavailable.
     */
    private fun getExternalPath(): String {
        val externalDir = context.getExternalFilesDir(null)
        return when {
            isExternalStorageAvailable() && externalDir != null -> externalDir.absolutePath
            context.filesDir != null -> {
                // Fallback to internal storage if external is not available
                context.filesDir.absolutePath
            }
            else -> throw IllegalStateException("Neither external nor internal storage is available")
        }
    }

    /**
     * Checks if external storage is currently available and writable.
     *
     * @return true if external storage is available and writable, false otherwise.
     */
    private fun isExternalStorageAvailable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }
}
