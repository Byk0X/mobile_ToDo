package com.example.mobile_todo.composables

import androidx.compose.material3.AlertDialog
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobile_todo.database.TaskWithAttachemnts

@Composable
fun TaskDetailDialog(
    taskWithAttachments: TaskWithAttachemnts,
    onDismiss: () -> Unit,
    onDelete: (TaskWithAttachemnts) -> Unit,
    onEdit: (TaskWithAttachemnts) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(taskWithAttachments.task.title) },
        text = {
            Column {
                Text("Opis: ${taskWithAttachments.task.description}")
                Text("Kategoria: ${taskWithAttachments.task.category}")
                Text("Termin: ${taskWithAttachments.task.dueAt}")
                Text("Zakończone: ${if (taskWithAttachments.task.status) "Tak" else "Nie"}")
                Text("Powiadomienie: ${if (taskWithAttachments.task.hasNotification) "Tak" else "Nie"}")

                Spacer(Modifier.height(8.dp))
                Text("Załączniki:")
                taskWithAttachments.attachments.forEach {
                    Text("- ${Uri.parse(it.uri).lastPathSegment}")
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Zamknij")
            }
        },
        dismissButton = {
            Column {
                Button(
                    onClick = {
                        onEdit(taskWithAttachments)
                        onDismiss()
                    }
                ) {
                    Text("Edytuj")
                }

                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = {
                        onDelete(taskWithAttachments)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Usuń")
                }
            }
        }
    )
}
