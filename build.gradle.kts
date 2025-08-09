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
    alias(libs.plugins.detekt) apply false
}

subprojects {
    // Apply Detekt to all modules
    plugins.apply("io.gitlab.arturbosch.detekt")

    dependencies {
        val detektVersion = "1.23.8"
        // Enable ktlint-backed formatting rules inside Detekt
        add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
    }

    // Configure Detekt
    extensions.configure(io.gitlab.arturbosch.detekt.extensions.DetektExtension::class.java) {
        // point to the config file we’ll add below
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        allRules = false
        parallel = true
        autoCorrect = true
    }

    // Make `./gradlew check` run detekt (defer wiring until tasks are realized)
    afterEvaluate {
        tasks.findByName("check")?.dependsOn("detekt")
    }
}