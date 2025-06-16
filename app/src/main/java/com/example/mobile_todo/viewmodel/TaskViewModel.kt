package com.example.mobile_todo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_todo.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class TaskViewModel : ViewModel() {

    val categories = listOf("Dom", "Praca", "Szkoła", "Bez kategorii")

    private val _hideCompletedTasks = MutableStateFlow(false)
    val hideCompletedTasks: StateFlow<Boolean> = _hideCompletedTasks

    fun setHideCompletedTasks(hide: Boolean) {
        _hideCompletedTasks.value = hide
    }


    private val _selectedCategories = MutableStateFlow<List<String>>(categories)
    val selectedCategories: StateFlow<List<String>> = _selectedCategories

    fun toggleCategorySelection(category: String) {
        val current = _selectedCategories.value.toMutableList()
        if (current.contains(category)) current.remove(category) else current.add(category)
        _selectedCategories.value = current
    }

    // Czas powiadomień w minutach
    private val _notificationTimeBefore = MutableStateFlow(15)
    val notificationTimeBefore: StateFlow<Int> = _notificationTimeBefore

    fun setNotificationTimeBefore(minutes: Int) {
        _notificationTimeBefore.value = minutes
    }

    private lateinit var taskDao: TaskDao

    private val _tasksWithAttachments = MutableStateFlow<List<TaskWithAttachemnts>>(emptyList())
    val tasksWithAttachments: StateFlow<List<TaskWithAttachemnts>> = _tasksWithAttachments

    fun init(context: Context) {
        val db = AppDatabaseProvider.getDatabase(context)
        taskDao = db.taskDao()
        fetchTasks()
    }

    fun fetchTasks() {
        viewModelScope.launch {
            val tasks = taskDao.getTasksWithAttachments()
            _tasksWithAttachments.value = tasks
        }
    }

    fun insertTaskWithAttachments(task: Task, attachments: List<Attachment>) {
        viewModelScope.launch {
            taskDao.insertTaskWithAttachments(task, attachments)
            fetchTasks()
        }
    }

//    fun deleteTask(task: Task) {
//        viewModelScope.launch {
//            taskDao.deleteTask(task)
//            fetchTasks()
//        }
//    }

    fun updateTaskWithAttachments(task: Task, attachments: List<Attachment>) {
        viewModelScope.launch {
            taskDao.updateTaskWithAttachments(task, attachments)
            fetchTasks()
        }
    }

    fun deleteTaskWithAttachments(context: Context, taskWithAttachments: TaskWithAttachemnts) {
        viewModelScope.launch {
            taskWithAttachments.attachments.forEach { attachment ->
                val file = File(context.filesDir, "attachments/${attachment.filename}")
                if (file.exists()) {
                    file.delete()
                }
            }

            taskDao.deleteTaskWithAttachments(taskWithAttachments.task)

            fetchTasks()
        }
    }



}