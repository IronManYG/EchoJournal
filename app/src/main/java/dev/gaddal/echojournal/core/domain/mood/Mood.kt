package dev.gaddal.echojournal.core.domain.mood

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import dev.gaddal.echojournal.R
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Excited25
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Excited35
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Excited80
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Excited95
import dev.gaddal.echojournal.core.presentation.designsystem.colors.ExcitedGradient
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Neutral25
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Neutral35
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Neutral80
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Neutral95
import dev.gaddal.echojournal.core.presentation.designsystem.colors.NeutralGradient
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Peaceful25
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Peaceful35
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Peaceful80
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Peaceful95
import dev.gaddal.echojournal.core.presentation.designsystem.colors.PeacefulGradient
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Sad25
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Sad35
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Sad80
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Sad95
import dev.gaddal.echojournal.core.presentation.designsystem.colors.SadGradient
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Stressed25
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Stressed35
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Stressed80
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Stressed95
import dev.gaddal.echojournal.core.presentation.designsystem.colors.StressedGradient

sealed class Mood(
    val title: String,
    val containerColor: Color,
    val iconTint: Color,
    val progressColor: Color,
    val progressTrackColor: Color,
    val textColor: Color,
    val tagTextColor: Color,
    val hatchTagColor: Color,
    val gradientColors: List<Color>,
    @DrawableRes val iconRes: Int,
    @DrawableRes val iconResActiveOff: Int,
) {

    data object Sad : Mood(
        title = "Sad",
        containerColor = Sad25,
        iconTint = Sad80,
        progressColor = Sad80,
        progressTrackColor = Sad35,
        textColor = Sad80,
        tagTextColor = Sad95,
        hatchTagColor = Sad35,
        gradientColors = SadGradient,
        iconRes = R.drawable.mood_sad,
        iconResActiveOff = R.drawable.mood_sad_active_off
    )

    data object Stressed : Mood(
        title = "Stressed",
        containerColor = Stressed25,
        iconTint = Stressed80,
        progressColor = Stressed80,
        progressTrackColor = Stressed35,
        textColor = Stressed80,
        tagTextColor = Stressed95,
        hatchTagColor = Stressed35,
        gradientColors = StressedGradient,
        iconRes = R.drawable.mood_stresses,
        iconResActiveOff = R.drawable.mood_stresses_active_off
    )

    data object Neutral : Mood(
        title = "Neutral",
        containerColor = Neutral25,
        iconTint = Neutral80,
        progressColor = Neutral80,
        progressTrackColor = Neutral35,
        textColor = Neutral80,
        tagTextColor = Neutral95,
        hatchTagColor = Neutral35,
        gradientColors = NeutralGradient,
        iconRes = R.drawable.mood_neutral,
        iconResActiveOff = R.drawable.mood_neutral_active_off
    )

    data object Peaceful : Mood(
        title = "Peaceful",
        containerColor = Peaceful25,
        iconTint = Peaceful80,
        progressColor = Peaceful80,
        progressTrackColor = Peaceful35,
        textColor = Peaceful80,
        tagTextColor = Peaceful95,
        hatchTagColor = Peaceful35,
        gradientColors = PeacefulGradient,
        iconRes = R.drawable.mood_peaceful,
        iconResActiveOff = R.drawable.mood_peaceful_active_off
    )

    data object Excited : Mood(
        title = "Excited",
        containerColor = Excited25,
        iconTint = Excited80,
        progressColor = Excited80,
        progressTrackColor = Excited35,
        textColor = Excited80,
        tagTextColor = Excited95,
        hatchTagColor = Excited35,
        gradientColors = ExcitedGradient,
        iconRes = R.drawable.mood_excited,
        iconResActiveOff = R.drawable.mood_excited_active_off
    )

    companion object {
        val all: List<Mood> = listOf(
            Stressed, Sad, Neutral, Peaceful, Excited
        )
    }
}
