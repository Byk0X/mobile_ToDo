package com.example.mobile_todo.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import java.util.*

@RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
fun scheduleNotification(
    context: Context,
    taskTitle: String,
    notificationTimeMillis: Long
) {
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("task_title", taskTitle)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        System.currentTimeMillis().toInt(), // unikalny ID
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        notificationTimeMillis,
        pendingIntent
    )
}