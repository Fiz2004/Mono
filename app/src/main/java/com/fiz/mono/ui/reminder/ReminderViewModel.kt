package com.fiz.mono.ui.reminder

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.fiz.mono.util.cancelNotifications

class ReminderViewModel : ViewModel() {
    fun setAlarm(
        isChecked: Boolean,
        time: Int,
        alarmManager: AlarmManager,
        notifyPendingIntent: PendingIntent,
        requireActivity: FragmentActivity
    ) {
        when (isChecked) {
            true -> startTimer(time, alarmManager, notifyPendingIntent, requireActivity)
            false -> cancelNotification(alarmManager, notifyPendingIntent)
        }
    }

    private fun startTimer(
        timerLengthSelection: Int,
        alarmManager: AlarmManager,
        notifyPendingIntent: PendingIntent,
        requireActivity: FragmentActivity
    ) {
        val selectedInterval = timerLengthSelection * 1000
        val triggerTime = SystemClock.elapsedRealtime() + selectedInterval

        val notificationManager =
            ContextCompat.getSystemService(
                requireActivity,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.cancelNotifications()

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            notifyPendingIntent
        )
    }

    private fun cancelNotification(alarmManager: AlarmManager, notifyPendingIntent: PendingIntent) {
        alarmManager.cancel(notifyPendingIntent)
    }

}