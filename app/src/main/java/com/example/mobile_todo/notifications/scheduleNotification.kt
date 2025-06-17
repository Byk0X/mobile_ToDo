package com.example.mobile_todo.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import java.util.*

@RequiresApi(Build.VERSION_CODES.S)
@RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
fun scheduleNotification(
    context: Context,
    taskId: Int,
    taskTitle: String,
    notificationTimeMillis: Long
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (!alarmManager.canScheduleExactAlarms()) {
        val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        return
    }

    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("task_id", taskId)
        putExtra("task_title", taskTitle)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        taskId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        notificationTimeMillis,
        pendingIntent
    )
}
