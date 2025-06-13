package com.example.mobile_todo.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date

@Entity(tableName = "tasks")
data class Task(

    @PrimaryKey(autoGenerate = true) val id: Long =0,
    var title: String,
    val description: String,
    val createdAt: Date?,
    val dueAt: Date?,
    var status: Boolean,
    var hasNotification: Boolean,
    val category: String
)

@Entity(
    tableName = "attachments",
    foreignKeys = [ForeignKey(
        entity = Task::class,
        parentColumns = ["id"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("taskId")]
)
data class Attachment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val uri: String // np. content:// lub file://
)


data class TaskWithAttachemnts(

    @Embedded val task: Task,


    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )

    val attachments: List<Attachment>
)
