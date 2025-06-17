package com.example.mobile_todo.composables


import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import canScheduleExactAlarms
import com.example.mobile_todo.database.Attachment
import com.example.mobile_todo.database.Task
import com.example.mobile_todo.notifications.NotificationReceiver
import com.example.mobile_todo.notifications.scheduleNotification
import com.example.mobile_todo.utils.copyUriToInternalStorage
import com.example.mobile_todo.viewmodel.TaskViewModel
import java.sql.Date
import java.util.Calendar


@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    viewModel : TaskViewModel,
    onDismiss: () -> Unit,
    onSave: (Task, List<Attachment>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val createdAt = remember { Date(System.currentTimeMillis()) }
    var dueAt by remember { mutableStateOf<Calendar?>(null) }
    var status by remember { mutableStateOf(false) }
    var hasNotification by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("Bez kategorii") }
    val attachments = remember { mutableStateListOf<Uri>() }


    // File Picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                attachments.addAll(uris)
            }
        }
    )


    // Date picker
    val context = LocalContext.current
    fun showTimePicker(calendar: Calendar, onTimeSelected: (Calendar) -> Unit) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        android.app.TimePickerDialog(
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
                // Po wybraniu daty pokazujemy TimePicker
                showTimePicker(calendar) { calendarWithTime ->
                    onDateTimeSelected(calendarWithTime)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }




    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj zadanie") },
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
                    showDatePicker { selectedDateTime ->
                        dueAt = selectedDateTime
                    }
                }) {
                    Text(
                        dueAt?.let {
                            val y = it.get(Calendar.YEAR)
                            val m = it.get(Calendar.MONTH) + 1
                            val d = it.get(Calendar.DAY_OF_MONTH)
                            val h = it.get(Calendar.HOUR_OF_DAY)
                            val min = it.get(Calendar.MINUTE)
                            "Termin: $y-$m-$d $h:$min"
                        } ?: "Wybierz termin wykonania"
                    )
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

                attachments.forEachIndexed { index, uri ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            uri.lastPathSegment ?: "Załącznik",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { attachments.removeAt(index) }) {
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
                    val newTask = Task(
                        title = title,
                        description = description,
                        createdAt = createdAt,
                        dueAt = dueAt?.time,
                        status = status,
                        hasNotification = hasNotification,
                        category = category
                    )

                    val attachmentEntities = attachments.mapNotNull { uri ->
                        val localUri = copyUriToInternalStorage(context, uri)
                        localUri?.let {
                            Attachment(taskId = 0L, filename = it.toString())
                        }
                    }

                    onSave(newTask, attachmentEntities)
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