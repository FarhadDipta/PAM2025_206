package com.example.spms.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun InfoDialog(
    title: String,
    contentText: String,
    onDismiss: () -> Unit,
    confirmText: String = "OK"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(contentText) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(confirmText)
            }
        }
    )
}