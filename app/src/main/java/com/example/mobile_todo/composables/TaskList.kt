package com.example.mobile_todo.composables

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
import androidx.compose.ui.unit.dp
import com.example.mobile_todo.viewmodel.TaskViewModel

@Composable
fun TaskList(viewModel: TaskViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    val tasks = remember { mutableStateListOf("Zadanie 1", "Zadanie 2", "Zadanie 3") }

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
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = task,
                        modifier = Modifier.padding(16.dp)
                    )
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
                onSave = { task, attachments ->
                    viewModel.insertTaskWithAttachments(task, attachments)
                    showDialog = false
                }
            )
        }
    }
}






