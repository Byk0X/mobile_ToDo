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
    var searchQuery by remember { mutableStateOf("") }
    val hideCompleted by viewModel.hideCompletedTasks.collectAsState()
    val selectedCategories by viewModel.selectedCategories.collectAsState()

    val tasks by viewModel.tasksWithAttachments.collectAsState(initial = emptyList())

    val context = LocalContext.current

    val filteredTasks = tasks
        .filter { taskWithAttachments ->
            val task = taskWithAttachments.task
            println("Zadanie: ${task.title}, kategoria: ${task.category}, matchesCategory: ${selectedCategories.contains(task.category)}")
            val matchesQuery = task.title.contains(searchQuery, ignoreCase = true) ||
                    task.description.contains(searchQuery, ignoreCase = true)

            val matchesCompletion = if (hideCompleted) !task.status else true

            val matchesCategory = selectedCategories.contains(task.category)

            matchesQuery && matchesCompletion && matchesCategory
        }
        .sortedWith(compareBy { it.task.dueAt?.time ?: Long.MAX_VALUE })

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Pasek wyszukiwania
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Szukaj zadania...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 72.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(filteredTasks) { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedTask = task },
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (task.task.status) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = task.task.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (task.task.status) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = task.task.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (task.task.status) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button
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

        // Dialogi
        if (showDialog) {
            AddTaskDialog(
                viewModel,
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
                viewModel,
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