// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Make each plugin available to sub-projects/modules
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // If using Kotlin serialization, KSP, or Room in any modules:
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false

    // Kotlin code style/linting
    alias(libs.plugins.ktlint) apply false
}