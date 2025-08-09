package dev.gaddal.echojournal.journal.entries.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.R
import dev.gaddal.echojournal.core.domain.logs.topic.Topic

@Composable
fun TopicDropdownMenuItem(
    topic: Topic,
    isSelected: Boolean,
    onTopicSelected: (Topic) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.05f)
                } else {
                    Color.Unspecified
                }
            ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Tag,
                contentDescription = stringResource(R.string.tag),
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
                    contentDescription = stringResource(R.string.selected),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}
