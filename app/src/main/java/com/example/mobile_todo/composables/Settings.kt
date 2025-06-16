package com.example.mobile_todo.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobile_todo.viewmodel.TaskViewModel
import androidx.compose.runtime.getValue

@Composable
fun Settings(
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val hideCompleted by viewModel.hideCompletedTasks.collectAsState()
    val selectedCategories by viewModel.selectedCategories.collectAsState()
    val notificationTimeBefore by viewModel.notificationTimeBefore.collectAsState()


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Ustawienia aplikacji", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(24.dp))

        // Ukrywanie ukończonych zadań
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = hideCompleted,
                onCheckedChange = { viewModel.setHideCompletedTasks(it) }
            )
            Spacer(Modifier.width(8.dp))
            Text("Ukryj zakończone zadania")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Wybór kategorii zadań widocznych na liście
        Text("Kategorie widoczne na liście:", style = MaterialTheme.typography.titleMedium)
        viewModel.categories.forEach { category ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = selectedCategories.contains(category),
                    onCheckedChange = {
                        viewModel.toggleCategorySelection(category)
                    }
                )
                Spacer(Modifier.width(8.dp))
                Text(category)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Czas powiadomień przed wykonaniem
        Text("Czas powiadomień (minuty przed):", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = notificationTimeBefore.toFloat(),
            onValueChange = { viewModel.setNotificationTimeBefore(it.toInt()) },
            valueRange = 0f..60f,
            steps = 11
        )
        Text("$notificationTimeBefore minut")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onBack, modifier = Modifier.align(Alignment.End)) {
            Text("Zapisz i wróć")
        }
    }
}
