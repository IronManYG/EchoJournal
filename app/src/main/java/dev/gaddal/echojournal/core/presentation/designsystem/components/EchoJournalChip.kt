package dev.gaddal.echojournal.core.presentation.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.domain.mood.Mood
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme
import dev.gaddal.echojournal.core.sample.SampleData.sampleTopics

@Composable
fun EchoJournalChip(
    moods: List<Mood>? = null,
    topics: List<Topic>? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    onClearFilter: (() -> Unit)? = null,
    isDropdownOpen: Boolean = false, // <--- externally controlled
    onChipClick: () -> Unit = {}, // <--- callback to open/close from outside
) {
    // Determine if moods or topics are in play
    val hasMoods = !moods.isNullOrEmpty()
    val hasTopics = !topics.isNullOrEmpty()

    // Build the label text based on moods or topics
    val labelText = when {
        hasMoods -> buildMoodsLabel(moods!!)
        hasTopics -> buildTopicsLabel(topics!!)
        else -> {
            // Fallback when no moods/topics are selected.
            // Could also differentiate between "All Moods" / "All Topics"
            // if you know which type it is meant to represent.
            if (moods != null) {
                "All Moods"
            } else if (topics != null) {
                "All Topics"
            } else {
                "All Items"
            }
        }
    }

    // Leading icon(s) if moods are present. For topics, typically no icon needed.
    val leadingIcon: @Composable (() -> Unit)? = if (hasMoods) {
        { MoodsLeadingIcon(moods!!) }
    } else {
        null
    }

    // If we have an active filter (moods/topics selected), show a trailing X to clear
    val activeFilter = hasMoods || hasTopics
    val trailingIcon: @Composable (() -> Unit)? = if (activeFilter && onClearFilter != null) {
        {
            IconButton(
                onClick = onClearFilter,
                modifier = Modifier.size(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close, // or Icons.Outlined.Close
                    contentDescription = "Clear filter",
                    tint = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    } else {
        null
    }

    ElevatedAssistChip(
        onClick = {
            // Let parent handle toggling the menu
            onChipClick()
        },
        label = {
            Text(
                text = labelText,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelLarge
            )
        },
        modifier = modifier.padding(horizontal = 3.dp),
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = CircleShape,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = when {
                !selected && !activeFilter -> Color.Transparent
                else -> MaterialTheme.colorScheme.surface
            },
        ),
        elevation = when {
            !selected && !activeFilter -> AssistChipDefaults.assistChipElevation()
            selected -> AssistChipDefaults.assistChipElevation()
            else -> AssistChipDefaults.elevatedAssistChipElevation(elevation = 6.dp)
        },
        border = AssistChipDefaults.assistChipBorder(
            enabled = enabled,
            borderColor = when {
                !selected && !activeFilter -> MaterialTheme.colorScheme.outlineVariant
                selected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surface
            }
        )
    )
}

/**
 * Builds a label for selected moods:
 * - If 1 mood: "MoodName"
 * - If 2 moods: "Mood1, Mood2"
 * - If 3+ moods: "Mood1, Mood2 +X"
 *
 * Sorted alphabetically by mood name (or any property you wish).
 */
private fun buildMoodsLabel(moods: List<Mood>): String {
    // Map your Mood objects to a string name to sort them.
    // For example, using the simple name of the mood class or a custom property.
    val moodNames = moods.map { it::class.simpleName.orEmpty() }.sorted()
    return when (moodNames.size) {
        0 -> "All Moods"
        1 -> moodNames.first()
        2 -> "${moodNames[0]}, ${moodNames[1]}"
        else -> "${moodNames[0]}, ${moodNames[1]} +${moodNames.size - 2}"
    }
}

/**
 * Builds a label for selected topics:
 * - If 1 topic: "TopicName"
 * - If 2 topics: "Topic1, Topic2"
 * - If 3+ topics: "Topic1, Topic2 +X"
 */
private fun buildTopicsLabel(topics: List<Topic>): String {
    val sortedTopics = topics.map { it.name }.sorted()
    return when (sortedTopics.size) {
        0 -> "All Topics"
        1 -> sortedTopics.first()
        2 -> "${sortedTopics[0]}, ${sortedTopics[1]}"
        else -> "${sortedTopics[0]}, ${sortedTopics[1]} +${sortedTopics.size - 2}"
    }
}

/**
 * Leading icon(s) for moods:
 * - If 1 mood, show its icon.
 * - If 2 moods, show 2 icons overlapping.
 * - If 3+ moods, still show only 2 icons.
 *   (Adjust spacing/overlap as desired.)
 */
@Composable
private fun MoodsLeadingIcon(moods: List<Mood>) {
    val iconCount = minOf(moods.size, 2)
    if (iconCount == 1) {
        Image(
            painter = painterResource(id = moods.first().iconRes),
            contentDescription = moods.first()::class.simpleName,
            modifier = Modifier.size(22.dp)
        )
    } else {
        val firstMood = moods[0]
        val secondMood = moods[1]
        Box(modifier = Modifier.size(width = 42.dp, height = 22.dp)) {
            Image(
                painter = painterResource(id = firstMood.iconRes),
                contentDescription = firstMood::class.simpleName,
                modifier = Modifier.size(22.dp)
            )
            Image(
                painter = painterResource(id = secondMood.iconRes),
                contentDescription = secondMood::class.simpleName,
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .size(22.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EchoJournalChipPreview() {
    EchoJournalTheme {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EchoJournalChip(
                topics = emptyList(),
                onClearFilter = { /* Clear filter logic */ }
            )
            EchoJournalChip(
                topics = sampleTopics.subList(0, 2),
                selected = false,
                onClearFilter = { /* Clear filter logic */ }
            )
            EchoJournalChip(
                topics = sampleTopics.subList(0, 2),
                selected = true,
                onClearFilter = { /* Clear filter logic */ }
            )
            EchoJournalChip(
                topics = sampleTopics.subList(0, 4),
                selected = false,
                onClearFilter = { /* Clear filter logic */ }
            )
            EchoJournalChip(
                moods = emptyList(),
                onClearFilter = { /* Clear filter logic */ }
            )
            EchoJournalChip(
                moods = listOf(Mood.Stressed, Mood.Sad),
                selected = false,
                onClearFilter = { /* Clear filter logic */ }
            )
            EchoJournalChip(
                moods = listOf(Mood.Stressed, Mood.Sad),
                selected = true,
                onClearFilter = { /* Clear filter logic */ }
            )
            EchoJournalChip(
                moods = listOf(Mood.Stressed, Mood.Sad, Mood.Peaceful, Mood.Excited, Mood.Neutral),
                selected = false,
                onClearFilter = { /* Clear filter logic */ }
            )
        }
    }
}
