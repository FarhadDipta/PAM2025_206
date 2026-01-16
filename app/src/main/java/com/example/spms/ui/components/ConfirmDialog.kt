package com.example.spms.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewDialog(
    title: String,
    contentText: String,
    onCancel: () -> Unit,
    onSubmit: () -> Unit,
    isSubmitting: Boolean
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(title) },
        text = { Text(contentText) },
        confirmButton = {
            Button(onClick = onSubmit, enabled = !isSubmitting) {
                Text(if (isSubmitting) "Memproses..." else "Submit")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancel, enabled = !isSubmitting) {
                Text("Cancel")
            }
        }
    )
}