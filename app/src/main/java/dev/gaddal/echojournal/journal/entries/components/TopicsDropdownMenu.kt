package dev.gaddal.echojournal.journal.entries.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import dev.gaddal.echojournal.core.domain.logs.topic.Topic

/**
 * A Material 3 DropdownMenu for displaying and selecting topics.
 * Primarily intended for use when triggered by icon or button clicks,
 * offering standard Material Design dropdown behavior and positioning.
 */
@Composable
fun TopicsDropdownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    properties: PopupProperties = PopupProperties(),
    allTopics: List<Topic>,
    selectedTopics: List<Topic>,
    onTopicClick: (Topic) -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .fillMaxWidth(0.9f)
            .heightIn(max = 200.dp),
        properties = properties,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        allTopics.forEach { topic ->
            val isSelected = topic in selectedTopics
             TopicDropdownMenuItem(
                topic = topic,
                isSelected = isSelected,
                onTopicSelected = {
                    onTopicClick(topic)
                },
            )
        }
    }
}
