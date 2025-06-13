package com.example.mobile_todo.viewmodel


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_todo.database.AppDatabase
import com.example.mobile_todo.database.AppDatabaseProvider
import com.example.mobile_todo.database.Attachment
import com.example.mobile_todo.database.Task
import com.example.mobile_todo.database.TaskDao
import com.example.mobile_todo.database.TaskWithAttachemnts
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel() : ViewModel() {


    private lateinit var  taskDao : TaskDao


    fun init(context: Context) {
        val db = AppDatabaseProvider.getDatabase(context)
        taskDao = db.taskDao()
    }

    fun insertTaskWithAttachments(task: Task, attachments: List<Attachment>) {
        viewModelScope.launch {
            taskDao.insertTaskWithAttachments(task, attachments)
        }
    }

}