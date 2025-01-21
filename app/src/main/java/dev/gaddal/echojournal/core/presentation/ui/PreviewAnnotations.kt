package dev.gaddal.echojournal.core.presentation.ui

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Annotation for previewing UI components in different locales (languages).
 *
 * This annotation previews the UI in both Arabic and English locales.
 */
@Preview(showBackground = true, locale = "ar", name = "Arabic")
@Preview(showBackground = true, locale = "en", name = "English")
annotation class LocalesPreview

/**
 * Annotation for previewing UI components in different themes (light and dark).
 *
 * This annotation previews the UI in both Light and Dark themes.
 */
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark")
annotation class ThemesPreview

/**
 * Annotation for previewing UI components with different font scales (sizes).
 *
 * This annotation previews the UI with various font scales to test responsiveness.
 */
@Preview(showBackground = true, fontScale = 1.0f, name = "Default (100%)")
@Preview(showBackground = true, fontScale = 0.85f, name = "Small (85%)")
@Preview(showBackground = true, fontScale = 1.15f, name = "Large (115%)")
@Preview(showBackground = true, fontScale = 1.3f, name = "Largest (130%)")
annotation class FontScalePreview

/**
 * Annotation for previewing UI components on different devices.
 *
 * This annotation previews the UI on a phone, foldable device, custom device, and desktop.
 */
@Preview(showBackground = true, name = "Phone", device = "id:pixel_9")
@Preview(showBackground = true, name = "Foldable", device = "id:pixel_9_pro_fold")
@Preview(showBackground = true, name = "Tablet", device = "id:pixel_c")
@Preview(showBackground = true, name = "Desktop", device = "id:desktop_medium")
annotation class ReferenceDevices
