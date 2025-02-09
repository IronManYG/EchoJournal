package dev.gaddal.echojournal.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.core.domain.mood.Mood
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme

@Composable
fun EchoJournalTopic(
    text: String,
    mood: Mood? = null,
    // If mood is null, these become the fallback defaults:
    containerColor: Color = Color(0xFFF2F2F7),
    hatchTagColor: Color = MaterialTheme.colorScheme.outline,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    // Determine final colors based on whether mood is provided
    val finalContainerColor = mood?.containerColor ?: containerColor
    val finalHatchTagColor = mood?.hatchTagColor ?: hatchTagColor
    val finalTextColor = mood?.tagTextColor ?: textColor
    Row(
        modifier = Modifier
            .height(22.dp)
            .clip(CircleShape)
            .background(finalContainerColor)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "#",
            color = finalHatchTagColor,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = text,
            color = finalTextColor,
            fontWeight = FontWeight.W500,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EchoJournalTopicPreview() {
    EchoJournalTheme {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EchoJournalTopic(
                text = "Topic"
            )

            // Moods
            EchoJournalTopic(
                text = "Sad",
                mood = Mood.Sad
            )

            EchoJournalTopic(
                text = "Stressed",
                mood = Mood.Stressed
            )

            EchoJournalTopic(
                text = "Neutral",
                mood = Mood.Neutral
            )

            EchoJournalTopic(
                text = "Peaceful",
                mood = Mood.Peaceful
            )

            EchoJournalTopic(
                text = "Excited",
                mood = Mood.Excited
            )
        }
    }
}