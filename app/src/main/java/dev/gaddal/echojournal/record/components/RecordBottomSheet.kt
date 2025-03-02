@file:OptIn(ExperimentalMaterial3Api::class)

package dev.gaddal.echojournal.record.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.core.domain.util.formatted
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Secondary95
import dev.gaddal.echojournal.core.presentation.designsystem.components.EchoJournalFAB
import dev.gaddal.echojournal.journal.entries.EntriesAction
import dev.gaddal.echojournal.journal.entries.EntriesState
import dev.gaddal.echojournal.record.util.findActivity
import dev.gaddal.echojournal.record.util.hasRecordAudioPermission
import dev.gaddal.echojournal.record.util.shouldShowRecordAudioPermissionRationale

@Composable
fun RecordBottomSheet(
    showBottomSheet: Boolean,
    onDismiss: () -> Unit,
    onAction: (EntriesAction) -> Unit,
    state: EntriesState, // from ViewModel
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // 1) Permission launcher: notify ViewModel when user grants/denies mic permission
    val requestAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            val showRationale = (!granted && activity != null &&
                    activity.shouldShowRecordAudioPermissionRationale())
            onAction(
                EntriesAction.SubmitAudioPermissionInfo(
                    acceptedAudioPermission = granted,
                    showAudioRationale = showRationale
                )
            )
        }
    )

    // 2) If bottom sheet is shown, do an immediate permission check
    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet && activity != null) {
            val alreadyGranted = context.hasRecordAudioPermission()
            val rationale = activity.shouldShowRecordAudioPermissionRationale()

            onAction(
                EntriesAction.SubmitAudioPermissionInfo(
                    acceptedAudioPermission = alreadyGranted,
                    showAudioRationale = rationale && !alreadyGranted
                )
            )

            // If user doesn't have permission & no rationale needed -> request right away
            if (!alreadyGranted && !rationale) {
                requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    // 3) Start recording *immediately* if we have permission and are not paused/recording yet.
    LaunchedEffect(showBottomSheet, state.hasRecordPermission) {
        // Only start if bottom sheet is open & we have permission
        // and user is not already recording or paused
        if (showBottomSheet &&
            state.hasRecordPermission &&
            !state.isRecording &&
            !state.isPaused
        ) {
            onAction(EntriesAction.OnStartRecordingClick)
        }
    }

    // 4) Show the bottom sheet only if needed
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                // if user dismiss, we treat it like "Finish" (or you could do "Cancel")
                onAction(EntriesAction.OnCancelRecordingClick)
                onDismiss()
            },
            modifier = modifier,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            // Header
            Text(
                text = if (state.isPaused ) "Recording paused" else "Recording your memories...",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Show the elapsed time in HH:MM:SS format
            Text(
                text = state.elapsedTime.formatted(),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .height(128.dp + 64.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT button: "Cancel" -> fully discard
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer,
                ) {
                    IconButton(
                        onClick = {
                            onAction(EntriesAction.OnCancelRecordingClick)
                            onDismiss()
                        },
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                // MIDDLE button: Start or Resume (mic icon) [but if user is paused, show "mic"?]
                EchoJournalFAB(
                    modifier = Modifier.weight(1f),
                    icon = if (state.isPaused) Icons.Default.Mic else Icons.Default.Check,
                    isLargeVariant = true,
                    rippleEnabled = true,
                    onClick = {
                        // If user doesn't have permission, request again
                        if (!state.hasRecordPermission) {
                            requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            when {
                                // If not recording or paused -> Start
                                !state.isRecording && !state.isPaused -> {
                                    onAction(EntriesAction.OnStartRecordingClick)
                                }
                                // If paused -> Resume
                                state.isPaused -> {
                                    onAction(EntriesAction.OnResumeRecordingClick)
                                }

                                else -> {
                                    onAction(EntriesAction.OnFinishRecordingClick)
                                    onAction(EntriesAction.OnCreateNewEntryTrigger)
                                    onDismiss()
                                }
                            }
                        }
                    }
                )

                // RIGHT button: Pause or Finish
                Surface(
                    shape = CircleShape,
                    color = Secondary95,
                ) {
                    IconButton(
                        onClick = {
                            if (!state.isPaused) {
                                // If actively recording -> Pause
                                onAction(EntriesAction.OnPauseRecordingClick)
                            } else {
                                // If already paused -> Finish
                                onAction(EntriesAction.OnFinishRecordingClick)
                                onAction(EntriesAction.OnCreateNewEntryTrigger)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = if (!state.isPaused) Icons.Default.Pause else Icons.Default.Check,
                            contentDescription = if (!state.isPaused) "Pause" else "Finish",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    // 4) Show a rationale dialog if the VM says so
    if (state.showRecordRationale) {
        AlertDialog(
            onDismissRequest = {
                // Typically you might not let them just “dismiss,” but it’s up to you
                onAction(EntriesAction.OnDismissRationaleDialog)
            },
            title = { Text("Microphone Permission Required") },
            text = {
                Text(
                    "We need microphone access to record audio. " +
                            "Without it, we can’t capture your memories."
                )
            },
            confirmButton = {
                Button(onClick = {
                    // Re-request after user sees rationale
                    onAction(EntriesAction.OnDismissRationaleDialog)
                    requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }) {
                    Text("Allow")
                }
            },
            dismissButton = {
                Button(onClick = {
                    onAction(EntriesAction.OnDismissRationaleDialog)
                }) {
                    Text("No thanks")
                }
            }
        )
    }
}


@Preview
@Composable
fun RecordBottomSheetPreview() {
    EchoJournalTheme {
        RecordBottomSheet(
            showBottomSheet = true,
            onDismiss = { },
            onAction = { },
            state = EntriesState(
                // For example, pretend user has permission but is not started yet
                hasRecordPermission = true,
                isRecording = false,
                isPaused = false,
                elapsedTime = kotlin.time.Duration.ZERO
            )
        )
    }
}
