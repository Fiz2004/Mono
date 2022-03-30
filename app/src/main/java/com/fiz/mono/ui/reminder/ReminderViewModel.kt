package com.fiz.mono.ui.reminder

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fiz.mono.util.cancelNotifications

class ReminderViewModel : ViewModel() {

    private val _hours = MutableLiveData(0)
    val hours: LiveData<Int> = _hours

    private val _minutes = MutableLiveData(0)
    val minutes: LiveData<Int> = _minutes

    val isCanReminder: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        isCanReminder.addSource(hours) {
            isCanReminder.value = it != 0 && _minutes.value != 0
        }
        isCanReminder.addSource(minutes) {
            isCanReminder.value = _hours.value != 0 && it != 0
        }
    }

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

    fun setHours(hours: Int) {
        _hours.value = hours
    }

    fun setMinutes(minutes: Int) {
        _minutes.value = minutes
    }

    fun hoursError() {
        _hours.value = 0
    }

    fun minutesError() {
        _minutes.value = 0
    }

}