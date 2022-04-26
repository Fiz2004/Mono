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

    fun start() {
        val hours = loadHours()
        val minutes = loadMinutes()
        setHours(hours)
        setMinutes(minutes)
        if (uiState.value.timeForReminder.isNotEmpty()) {
            onNotify()
        }
    }

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
        dataLocalDataSource.saveHour("")
        dataLocalDataSource.saveMinute("")
    }

    fun loadMinutes() = dataLocalDataSource.loadMinutes()

    fun loadHours() = dataLocalDataSource.loadHours()

    fun cancelNotification() {
        uiState.value = uiState.value
            .copy(isNotifyInstalled = false)
    }

    fun setHours(hour: String) {
        val timeForReminder = uiState.value.timeForReminder.copy(hour = hour)
        uiState.value = uiState.value
            .copy(timeForReminder = timeForReminder, isErrorHourEditText = !isStatusHour(hour))
    }

    fun setMinutes(minute: String) {
        val timeForReminder = uiState.value.timeForReminder.copy(minute = minute)
        uiState.value = uiState.value
            .copy(timeForReminder = timeForReminder, isErrorMinuteEditText = !isStatusMinute(minute))
    }

    private fun isStatusHour(hour: String): Boolean {
        if (hour.isBlank()) return true
        return try {
            val hourInt = hour.toInt()
            if (hourInt > 23)
                return false
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun isStatusMinute(minute: String): Boolean {
        if (minute.isBlank()) return true
        return try {
            val minuteInt = minute.toInt()
            if (minuteInt > 59)
                return false
            true
        } catch (e: Exception) {
            false
        }
    }
}