package com.fiz.mono.ui.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fiz.mono.R
import com.fiz.mono.receiver.AlarmReceiver
import com.fiz.mono.util.cancelNotifications
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor() : ViewModel() {

    private val _hours = MutableLiveData(0)
    val hours: LiveData<Int> = _hours

    private val _minutes = MutableLiveData(0)
    val minutes: LiveData<Int> = _minutes

    val isCanReminder: MediatorLiveData<Boolean> = MediatorLiveData()

    var notify = MutableLiveData(false)

    private lateinit var alarmManager: AlarmManager
    private lateinit var notifyIntent: Intent

    private lateinit var alarm: PendingIntent

    private lateinit var notifyPendingIntent: PendingIntent

    init {
        isCanReminder.addSource(hours) {
            isCanReminder.value = it != 0 && _minutes.value != 0
        }
        isCanReminder.addSource(minutes) {
            isCanReminder.value = _hours.value != 0 && it != 0
        }
    }

    fun createChannel(channelId: String, channelName: String, description: String, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = description

            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun init(context: Context) {
        alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        notifyIntent = Intent(context, AlarmReceiver::class.java)

        alarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_REMINDER,
                notifyIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_REMINDER,
                notifyIntent,
                PendingIntent.FLAG_NO_CREATE
            )
        }

        notifyPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_REMINDER,
                notifyIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_REMINDER,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    fun setAlarm(context: Context) {
        val currentTime = Calendar.getInstance()
        val needTime = Calendar.getInstance()
        val hours = hours.value ?: 0
        val minutes = minutes.value ?: 0

        needTime.set(Calendar.HOUR_OF_DAY, hours)
        needTime.set(Calendar.MINUTE, minutes)
        if (currentTime.get(Calendar.HOUR_OF_DAY) > needTime.get(Calendar.HOUR_OF_DAY) &&
            currentTime.get(Calendar.MINUTE) > needTime.get(Calendar.MINUTE)
        )
            needTime.add(Calendar.DATE, -1)

        val time = ((needTime.time.time - currentTime.time.time) / 1000).toInt()

        startTimer(time, alarmManager, notifyPendingIntent, context)

        notify.value = true
    }

    private fun startTimer(
        timerLengthSelection: Int,
        alarmManager: AlarmManager,
        notifyPendingIntent: PendingIntent,
        context: Context
    ) {
        val selectedInterval = timerLengthSelection * 1000
        val triggerTime = SystemClock.elapsedRealtime() + selectedInterval

        val notificationManager =
            ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.cancelNotifications()

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            notifyPendingIntent
        )

        context.getSharedPreferences(
            context.getString(R.string.preferences),
            AppCompatActivity.MODE_PRIVATE
        ).edit()
            .putInt("notify hours", hours.value ?: 0)
            .putInt("notify minutes", minutes.value ?: 0)
            .apply()
    }

    fun cancelNotification(context: Context) {
        notify.value = false
        alarmManager.cancel(notifyPendingIntent)
        val notificationManager =
            ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.cancelNotifications()
        context.getSharedPreferences(
            context.getString(R.string.preferences),
            AppCompatActivity.MODE_PRIVATE
        ).edit()
            .putInt("notify hours", 0)
            .putInt("notify minutes", 0)
            .apply()
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