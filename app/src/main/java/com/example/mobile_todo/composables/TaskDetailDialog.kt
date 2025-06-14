package com.example.mobile_todo.composables

import android.content.Intent
import androidx.compose.material3.AlertDialog
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.mobile_todo.database.TaskWithAttachemnts
import java.io.File

@Composable
fun TaskDetailDialog(
    taskWithAttachments: TaskWithAttachemnts,
    onDismiss: () -> Unit,
    onDelete: (TaskWithAttachemnts) -> Unit,
    onEdit: (TaskWithAttachemnts) -> Unit
) {
    val context = LocalContext.current

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
                    val file = File(context.filesDir, "attachments/${it.filename}")
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        file
                    )

                    Text(
                        text = "- ${uri.lastPathSegment}",
                        modifier = Modifier
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, context.contentResolver.getType(uri))
                                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Nie można otworzyć pliku", Toast.LENGTH_SHORT).show()
                                }
                            },
                        color = MaterialTheme.colorScheme.primary
                    )
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
                Button(onClick = {
                    onEdit(taskWithAttachments)
                    onDismiss()
                }) {
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

