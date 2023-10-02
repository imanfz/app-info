package dev.fztech.app.info.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import dev.fztech.app.info.ui.theme.AppInfoTheme
import dev.fztech.app.info.ui.theme.Dimens

@Composable
fun PermissionDialog(
    title: String,
    message: String,
    cancellable: Boolean = true,
    disableDismissButton: Boolean = false,
    dismissButtonText: String = "Cancel",
    actionButtonText: String,
    onAction: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = cancellable,
            dismissOnClickOutside = cancellable,
            securePolicy = SecureFlagPolicy.Inherit
        )
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier,
            shadowElevation = Dimens.Small
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .padding(Dimens.XLarge)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                ExtraLargeSpacer()

                Text(text = message, style = MaterialTheme.typography.bodyMedium)

                DefaultSpacer()

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (!disableDismissButton) {
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = dismissButtonText.uppercase(),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        DefaultSpacer()
                    }
                    TextButton(
                        onClick = onAction,
                    ) {
                        Text(
                            text = actionButtonText.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PermissionDialogPreview() {
    AppInfoTheme {
        PermissionDialog(
            title = "Missing Permission",
            message = "Storage permission is currently disabled. To be able to read storage capacity you need to accept this permission.",
            actionButtonText = "Setting",
            onAction = {  },
            onDismiss = {  }
        )
    }
}