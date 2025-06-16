import android.app.AlarmManager
import android.content.Context
import android.os.Build

fun canScheduleExactAlarms(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.canScheduleExactAlarms()
    }
    return true
}