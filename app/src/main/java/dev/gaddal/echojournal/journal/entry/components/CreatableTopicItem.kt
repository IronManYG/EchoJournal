package dev.gaddal.echojournal.journal.entry.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CreatableTopicItem(
    searchQuery: String,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    DropdownMenuItem(
        text = {
            Text(
                text = """
                            Create `$searchQuery`
                """.trimIndent(),
                color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium
            )
        },
        onClick = { onCreateClick() },
        modifier = modifier.padding(horizontal = 4.dp, vertical = 2.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add new topic",
                modifier = Modifier.size(20.dp),
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        },
        enabled = enabled,
    )
}
