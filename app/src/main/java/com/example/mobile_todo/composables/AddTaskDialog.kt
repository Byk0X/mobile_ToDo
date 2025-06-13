package com.example.mobile_todo.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.mobile_todo.database.Attachment
import com.example.mobile_todo.database.Task
import com.example.mobile_todo.database.TaskWithAttachemnts
import java.util.Date

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSave: (Task, List<Attachment>) -> Unit,
    existingTask: TaskWithAttachemnts? = null
) {
    var title by remember { mutableStateOf(TextFieldValue(existingTask?.task?.title ?: "")) }
    var description by remember { mutableStateOf(TextFieldValue(existingTask?.task?.description ?: "")) }
    var attachments by remember { mutableStateOf(existingTask?.attachments ?: emptyList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (existingTask == null) "Dodaj zadanie" else "Edytuj zadanie")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tytuł") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Opis") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Tu możesz dodać UI do zarządzania załącznikami, np. dodaj plik, lista nazw plików itd.
                Text("Załączników: ${attachments.size}")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val task = if (existingTask != null) {
                        existingTask.task.copy(
                            title = title.text,
                            description = description.text
                        )
                    } else {
                        Task(
                            title = title.text,
                            description = description.text,
                            createdAt = Date(System.currentTimeMillis()),
                            dueAt = null,
                            status = false,
                            hasNotification = false,
                            category = ""
                        )
                    }
                    onSave(task, attachments)
                }
            ) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}
