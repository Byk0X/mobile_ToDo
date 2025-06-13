package com.example.mobile_todo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: Task): Long

    @Insert
    suspend fun insertAttachments(attachments: List<Attachment>)

    @Transaction
    suspend fun insertTaskWithAttachments(task: Task, attachments: List<Attachment>) {
        val taskId = insertTask(task)
        val updatedAttachments = attachments.map { it.copy(taskId = taskId) }
        insertAttachments(updatedAttachments)
    }

    @Transaction
    @Query("SELECT * FROM tasks")
    suspend fun getTasksWithAttachments(): List<TaskWithAttachemnts>
}
