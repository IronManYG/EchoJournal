package dev.gaddal.echojournal.record.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

private fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasRecordAudioPermission(): Boolean {
    return hasPermission(Manifest.permission.RECORD_AUDIO)
}

fun ComponentActivity.shouldShowRecordAudioPermissionRationale(): Boolean {
    return shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
}
