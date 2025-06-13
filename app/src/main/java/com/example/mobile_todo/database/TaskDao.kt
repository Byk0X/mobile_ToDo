package com.example.mobile_todo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Delete

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: Task): Long

    @Insert
    suspend fun insertAttachments(attachments: List<Attachment>)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM attachments WHERE taskId = :taskId")
    suspend fun deleteAttachmentsByTaskId(taskId: Long)

    @Transaction
    suspend fun insertTaskWithAttachments(task: Task, attachments: List<Attachment>) {
        val taskId = insertTask(task)
        val updatedAttachments = attachments.map { it.copy(taskId = taskId) }
        insertAttachments(updatedAttachments)
    }

    @Transaction
    suspend fun updateTaskWithAttachments(task: Task, attachments: List<Attachment>) {
        updateTask(task)
        deleteAttachmentsByTaskId(task.id)
        insertAttachments(attachments.map { it.copy(taskId = task.id) })
    }

    @Transaction
    suspend fun deleteTaskWithAttachments(task: Task) {
        deleteAttachmentsByTaskId(task.id)
        deleteTask(task)
    }


    @Transaction
    @Query("SELECT * FROM tasks")
    suspend fun getTasksWithAttachments(): List<TaskWithAttachemnts>
}


