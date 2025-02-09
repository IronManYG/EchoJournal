package dev.gaddal.echojournal.journal.entries.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.core.domain.logs.topic.Topic

@Composable
fun TopicsDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    allTopics: List<Topic>,
    selectedTopics: List<Topic>,
    onTopicSelected: (Topic) -> Unit,
    isNewRecord: Boolean = false,
    newTopicName: String = "",
    onCreateTopic: (Topic) -> Unit = {}
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(0.9f),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        allTopics.forEach { topic ->
            val isSelected = topic in selectedTopics
            DropdownMenuItem(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.05f)
                        else Color.Unspecified
                    ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Tag,
                        contentDescription = "Tag",
                        modifier = Modifier.width(18.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                },
                text = {
                    Text(
                        text = topic.name,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                onClick = {
                    onTopicSelected(topic)
                },
                trailingIcon = {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
        // Show "Add new topic" if on Create Record screen
        if (isNewRecord) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = """
                            Create `$newTopicName`
                        """.trimIndent(),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                onClick = {
                    // You could open a Dialog or do anything else to get the actual name
                    // For now, let's do something simple:
                    onCreateTopic(
                        Topic(
                            id = -1,
                            name = newTopicName,
                            colorHex = null
                        )
                    )
                },
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add new topic",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }
}
