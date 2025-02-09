package dev.gaddal.echojournal.core.domain.mood

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import dev.gaddal.echojournal.R
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Excited25
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Excited35
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Excited80
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Excited95
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Neutral25
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Neutral35
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Neutral80
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Neutral95
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Peaceful25
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Peaceful35
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Peaceful80
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Peaceful95
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Sad25
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Sad35
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Sad80
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Sad95
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Stressed25
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Stressed35
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Stressed80
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Stressed95

sealed class Mood(
    val containerColor: Color,
    val iconTint: Color,
    val progressColor: Color,
    val progressTrackColor: Color,
    val textColor: Color,
    val tagTextColor: Color,
    val hatchTagColor: Color,
    @DrawableRes val iconRes: Int
) {

    data object Sad : Mood(
        containerColor = Sad25,
        iconTint = Sad80,
        progressColor = Sad80,
        progressTrackColor = Sad35,
        textColor = Sad80,
        tagTextColor = Sad95,
        hatchTagColor = Sad35,
        iconRes = R.drawable.mood_sad
    )

    data object Stressed : Mood(
        containerColor = Stressed25,
        iconTint = Stressed80,
        progressColor = Stressed80,
        progressTrackColor = Stressed35,
        textColor = Stressed80,
        tagTextColor = Stressed95,
        hatchTagColor = Stressed35,
        iconRes = R.drawable.mood_stresses
    )

    data object Neutral : Mood(
        containerColor = Neutral25,
        iconTint = Neutral80,
        progressColor = Neutral80,
        progressTrackColor = Neutral35,
        textColor = Neutral80,
        tagTextColor = Neutral95,
        hatchTagColor = Neutral35,
        iconRes = R.drawable.mood_neutral
    )

    data object Peaceful : Mood(
        containerColor = Peaceful25,
        iconTint = Peaceful80,
        progressColor = Peaceful80,
        progressTrackColor = Peaceful35,
        textColor = Peaceful80,
        tagTextColor = Peaceful95,
        hatchTagColor = Peaceful35,
        iconRes = R.drawable.mood_peaceful
    )

    data object Excited : Mood(
        containerColor = Excited25,
        iconTint = Excited80,
        progressColor = Excited80,
        progressTrackColor = Excited35,
        textColor = Excited80,
        tagTextColor = Excited95,
        hatchTagColor = Excited35,
        iconRes = R.drawable.mood_excited
    )

    companion object {
        val all: List<Mood> = listOf(
            Sad, Stressed, Neutral, Peaceful, Excited
        )
    }
}
