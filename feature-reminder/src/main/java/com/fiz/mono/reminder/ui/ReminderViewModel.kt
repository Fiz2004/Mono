package com.fiz.mono.reminder.ui

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import com.fiz.mono.reminder.data.DataLocalDataSourceImpl
import com.fiz.mono.reminder.domain.getCountSecondsBetween
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.threeten.bp.LocalTime
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(private val dataLocalDataSource: DataLocalDataSourceImpl) :
    ViewModel() {

    var uiState = MutableStateFlow(ReminderUiState()); private set

    fun getTriggerTime(): Long {
        val selectedInterval = getTimerLengthSelection(LocalTime.now()) * 1000
        return SystemClock.elapsedRealtime() + selectedInterval
    }

    private fun getTimerLengthSelection(now: LocalTime): Int {
        val needTime = uiState.value.timeForReminder.getLocalTime()

        return getCountSecondsBetween(now, needTime)
    }

    fun onNotify() {
        uiState.value = uiState.value
            .copy(isNotifyInstalled = true)
    }

    fun saveTime() {
        dataLocalDataSource.saveHour(uiState.value.timeForReminder.hour)
        dataLocalDataSource.saveMinute(uiState.value.timeForReminder.minute)
    }

    fun resetTime() {
        dataLocalDataSource.saveHour(0)
        dataLocalDataSource.saveMinute(0)
    }

    fun loadMinutes() = dataLocalDataSource.loadMinutes()

    fun loadHours() = dataLocalDataSource.loadHours()

    fun cancelNotification() {
        uiState.value = uiState.value
            .copy(isNotifyInstalled = false)
    }

    fun setHours(hour: String): Boolean {
        return try {
            val hourInt = hour.toInt()
            if (hourInt > 23)
                return false
            else {
                val timeForReminder = uiState.value.timeForReminder.copy(hour = hourInt)
                uiState.value = uiState.value
                    .copy(timeForReminder = timeForReminder)
            }
            true
        } catch (e: Exception) {
            if (hour != "") {
                return false
            }
            val timeForReminder = uiState.value.timeForReminder.copy(hour = 0)
            uiState.value = uiState.value
                .copy(timeForReminder = timeForReminder)
            false
        }
    }

    fun setMinutes(minute: String): Boolean {
        return try {
            val minuteInt = minute.toInt()
            if (minuteInt > 59)
                return false
            else {
                val timeForReminder = uiState.value.timeForReminder.copy(minute = minuteInt)
                uiState.value = uiState.value
                    .copy(timeForReminder = timeForReminder)
            }
            true
        } catch (e: Exception) {
            if (minute != "") {
                return false
            }
            val timeForReminder = uiState.value.timeForReminder.copy(minute = 0)
            uiState.value = uiState.value
                .copy(timeForReminder = timeForReminder)
            false
        }
    }

    fun start() {
        val hours = loadHours()
        val minutes = loadMinutes()

        if (hours != 0 && minutes != 0) {
            onNotify()
        }
    }

    fun canReminderNo() {
        uiState.value = uiState.value
            .copy(isCanReminder = false)
    }

    fun setCanReminder() {
        uiState.value = uiState.value
            .copy(isCanReminder = true)
    }
}