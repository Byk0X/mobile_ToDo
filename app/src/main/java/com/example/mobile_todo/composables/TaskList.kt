package com.example.mobile_todo.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mobile_todo.database.Attachment
import com.example.mobile_todo.database.Task
import com.example.mobile_todo.database.TaskWithAttachemnts
import com.example.mobile_todo.viewmodel.TaskViewModel

@Composable
fun TaskList(viewModel: TaskViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<TaskWithAttachemnts?>(null) }
    var taskToEdit by remember { mutableStateOf<TaskWithAttachemnts?>(null) }

    val tasks by viewModel.tasksWithAttachments.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(tasks) { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedTask = task },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = task.task.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(text = task.task.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Dodaj zadanie")
        }


        if (showDialog) {
            AddTaskDialog(
                onDismiss = { showDialog = false },
                onSave = { task: Task, attachments: List<Attachment> ->
                    viewModel.insertTaskWithAttachments(task, attachments)
                    showDialog = false
                }
            )
        }


        if (selectedTask != null) {
            TaskDetailDialog(
                taskWithAttachments = selectedTask!!,
                onDismiss = { selectedTask = null },
                onDelete = {
                    viewModel.deleteTaskWithAttachments(context, it)
                    selectedTask = null
                },
                onEdit = {
                    taskToEdit = it
                    selectedTask = null
                }
            )
        }

        if (taskToEdit != null) {
            EditTaskDialog(
                onDismiss = { taskToEdit = null },
                onSave = { updatedTask: Task, updatedAttachments: List<Attachment> ->
                    viewModel.updateTaskWithAttachments(updatedTask, updatedAttachments)
                    taskToEdit = null
                },
                existingTask = taskToEdit
            )
        }
    }
}
