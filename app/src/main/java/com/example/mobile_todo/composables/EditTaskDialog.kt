package com.example.mobile_todo.composables

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mobile_todo.database.Attachment
import com.example.mobile_todo.database.Task
import com.example.mobile_todo.database.TaskWithAttachemnts
import com.example.mobile_todo.utils.copyUriToInternalStorage
import com.example.mobile_todo.utils.deleteAttachmentFile
import java.sql.Date
import java.util.*

@Composable
fun EditTaskDialog(
    onDismiss: () -> Unit,
    onSave: (Task, List<Attachment>) -> Unit,
    existingTask: TaskWithAttachemnts?
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(existingTask?.task?.title ?: "") }
    var description by remember { mutableStateOf(existingTask?.task?.description ?: "") }
    val createdAt = existingTask?.task?.createdAt ?: Date(System.currentTimeMillis())
    var dueAt by remember { mutableStateOf(existingTask?.task?.dueAt) }
    var status by remember { mutableStateOf(existingTask?.task?.status ?: false) }
    var hasNotification by remember { mutableStateOf(existingTask?.task?.hasNotification ?: false) }
    var category by remember { mutableStateOf(existingTask?.task?.category ?: "Bez kategorii") }

    // attachments to lista nazw plików
    val attachments = remember {
        mutableStateListOf<String>().apply {
            existingTask?.attachments?.forEach { add(it.filename) }
        }
    }

    // Pomocnicza funkcja tworząca Uri z nazwy pliku w katalogu aplikacji
    fun getAttachmentUri(filename: String): Uri {
        val file = java.io.File(context.filesDir, filename)
        return Uri.fromFile(file)
    }

    // File Picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                uris.forEach { uri ->
                    // Kopiujemy plik do pamięci aplikacji, funkcja powinna zwracać nazwę pliku (String)
                    val filename = copyUriToInternalStorage(context, uri)
                    filename?.let {
                        attachments.add(it)
                    }
                }
            }
        }
    )

    // Date Picker
    val calendar = Calendar.getInstance()
    dueAt?.let { calendar.time = it }

    fun showDatePicker(onDateSelected: (Date) -> Unit) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                onDateSelected(Date(calendar.timeInMillis))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edytuj zadanie") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextField(value = title, onValueChange = { title = it }, label = { Text("Tytuł") })
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Opis") },
                    modifier = Modifier.height(100.dp)
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    showDatePicker { selectedDate -> dueAt = selectedDate }
                }) {
                    Text(dueAt?.let { "Termin: $it" } ?: "Wybierz termin wykonania")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = status, onCheckedChange = { status = it })
                    Text("Zakończone")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = hasNotification, onCheckedChange = { hasNotification = it })
                    Text("Powiadomienie")
                }

                Spacer(Modifier.height(8.dp))

                var categoryExpanded by remember { mutableStateOf(false) }

                Box {
                    Text(
                        text = "Kategoria: $category",
                        modifier = Modifier
                            .clickable { categoryExpanded = true }
                            .background(Color.LightGray)
                            .padding(8.dp)
                    )
                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        listOf("Bez kategorii", "Dom", "Praca", "Inne").forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    category = it
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Divider()

                Text("Załączniki (${attachments.size}):")

                attachments.forEachIndexed { index, filename ->
                    val uri = getAttachmentUri(filename)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            filename,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            deleteAttachmentFile(context, uri)
                            attachments.removeAt(index)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Usuń załącznik")
                        }
                    }
                }

                Button(onClick = {
                    launcher.launch(arrayOf("*/*"))
                }) {
                    Text("Dodaj załącznik")
                }
            }
        },
        confirmButton = {
            Button(
                enabled = title.isNotBlank(),
                onClick = {
                    val updatedTask = existingTask!!.task.copy(
                        title = title,
                        description = description,
                        createdAt = createdAt,
                        dueAt = dueAt,
                        status = status,
                        hasNotification = hasNotification,
                        category = category
                    )

                    // Tutaj mapujesz listę nazw plików na Attachment
                    val attachmentEntities = attachments.map { filename ->
                        Attachment(taskId = updatedTask.id, filename = filename)
                    }

                    onSave(updatedTask, attachmentEntities)
                    onDismiss()
                }
            ) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}

