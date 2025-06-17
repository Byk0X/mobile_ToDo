package com.example.mobile_todo.composables

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mobile_todo.database.Attachment
import com.example.mobile_todo.database.Task
import com.example.mobile_todo.database.TaskWithAttachemnts
import com.example.mobile_todo.utils.copyUriToInternalStorage
import com.example.mobile_todo.utils.deleteAttachmentFile
import com.example.mobile_todo.viewmodel.TaskViewModel
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    viewModel: TaskViewModel,
    onDismiss: () -> Unit,
    onSave: (Task, List<Attachment>) -> Unit,
    existingTask: TaskWithAttachemnts?
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(existingTask?.task?.title ?: "") }
    var description by remember { mutableStateOf(existingTask?.task?.description ?: "") }
    val createdAt = existingTask?.task?.createdAt ?: Date(System.currentTimeMillis())
    var dueAt by remember {
        mutableStateOf(existingTask?.task?.dueAt?.let {
            Calendar.getInstance().apply { time = it }
        })
    }
    var status by remember { mutableStateOf(existingTask?.task?.status ?: false) }
    var hasNotification by remember { mutableStateOf(existingTask?.task?.hasNotification ?: false) }
    var category by remember { mutableStateOf(existingTask?.task?.category ?: "Bez kategorii") }

    val attachments = remember {
        mutableStateListOf<String>().apply {
            existingTask?.attachments?.forEach { add(it.filename) }
        }
    }

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
                    val filename = copyUriToInternalStorage(context, uri)
                    filename?.let {
                        attachments.add(it)
                    }
                }
            }
        }
    )

    // Date & Time Picker
    fun showTimePicker(calendar: Calendar, onTimeSelected: (Calendar) -> Unit) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                onTimeSelected(calendar)
            },
            hour,
            minute,
            true
        ).show()
    }

    fun showDatePicker(onDateTimeSelected: (Calendar) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                showTimePicker(calendar) { onDateTimeSelected(it) }
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
                    showDatePicker { selected ->
                        dueAt = selected
                    }
                }) {
                    Text(dueAt?.let {
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        "Termin: ${sdf.format(it.time)}"
                    } ?: "Wybierz termin wykonania")
                }

                Spacer(Modifier.height(8.dp))

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

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        value = category,
                        onValueChange = {},
                        label = { Text("Kategoria") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        viewModel.categories.forEach {
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
                    val updatedTask = Task(
                        id = existingTask?.task?.id ?: 0L,
                        title = title,
                        description = description,
                        createdAt = createdAt,
                        dueAt = dueAt?.time,
                        status = status,
                        hasNotification = hasNotification,
                        category = category
                    )
                    val attachmentEntities = attachments.map {
                        Attachment(taskId = updatedTask.id, filename = it)
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
