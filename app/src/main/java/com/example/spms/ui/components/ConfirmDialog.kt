package com.example.spms.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewDialog(
    title: String,
    contentText: String,
    onCancel: () -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(title) },
        text = { Text(contentText) },
        confirmButton = {
            Button(onClick = onSubmit) {
                Text("Submit")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}