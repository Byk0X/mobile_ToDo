package com.example.mobile_todo.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.mobile_todo.MainActivity
import com.example.mobile_todo.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra("task_title") ?: "Zadanie"

        val notification = NotificationCompat.Builder(context, "task_channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Przypomnienie")
            .setContentText("Zbliża się zadanie: $taskTitle")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)

        // SPRAWDZENIE UPRWANIENIA (wymagane od Androida 13 / API 33)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}