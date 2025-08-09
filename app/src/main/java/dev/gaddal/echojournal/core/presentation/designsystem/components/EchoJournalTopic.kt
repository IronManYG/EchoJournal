package dev.gaddal.echojournal.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.domain.mood.Mood
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme

@Composable
fun EchoJournalTopic(
    topic: Topic,
    mood: Mood? = null,
    onClearTopicClick: ((Topic) -> Unit)? = null,
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
            text = topic.name,
            color = finalTextColor,
            fontWeight = FontWeight.W500,
            style = MaterialTheme.typography.bodySmall
        )
        if (onClearTopicClick != null) {
            IconButton(
                onClick = {
                    onClearTopicClick(topic)
                },
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear topic",
                    tint = finalHatchTagColor
                )
            }
        }
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
                topic = Topic(1, "Kotlin", "#FFFFFF"),
                onClearTopicClick = { /* Clear topic logic */ }
            )

            // Moods
            EchoJournalTopic(
                topic = Topic(2, "Sad", "#000000"),
                mood = Mood.Sad
            )

            EchoJournalTopic(
                topic = Topic(2, "Stressed", "#000000"),
                mood = Mood.Stressed
            )

            EchoJournalTopic(
                topic = Topic(2, "Sad", "#000000"),
                mood = Mood.Neutral
            )

            EchoJournalTopic(
                topic = Topic(2, "Peaceful", "#000000"),
                mood = Mood.Peaceful
            )

            EchoJournalTopic(
                topic = Topic(2, "Excited", "#000000"),
                mood = Mood.Excited
            )
        }
    }
}
