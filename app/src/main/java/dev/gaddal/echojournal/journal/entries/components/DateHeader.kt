package dev.gaddal.echojournal.journal.entries.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme

@Composable
fun DateHeader(
    label: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.labelMedium
    )
}

@Preview(showBackground = true)
@Composable
fun DateHeaderPreview() {
    EchoJournalTheme {
        DateHeader(label = "Today")
    }
}
