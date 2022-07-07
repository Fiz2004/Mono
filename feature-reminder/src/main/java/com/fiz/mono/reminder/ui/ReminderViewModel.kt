package com.fiz.mono.reminder.ui

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import com.fiz.mono.reminder.domain.DataLocalDataSource
import com.fiz.mono.reminder.domain.getCountSecondsBetween
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.threeten.bp.LocalTime
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(private val dataLocalDataSource: DataLocalDataSource) :
    ViewModel() {

    var viewState = MutableStateFlow(ReminderViewState()); private set

    fun start() {
        val hours = loadHours()
        val minutes = loadMinutes()
        setHours(hours)
        setMinutes(minutes)
        if (viewState.value.timeForReminder.isNotEmpty()) {
            onNotify()
        }
    }

    fun getTriggerTime(): Long {
        val selectedInterval = getTimerLengthSelection(LocalTime.now()) * 1000
        return SystemClock.elapsedRealtime() + selectedInterval
    }

    private fun getTimerLengthSelection(now: LocalTime): Int {
        val needTime = viewState.value.timeForReminder.getLocalTime()

        return getCountSecondsBetween(now, needTime)
    }

    fun onNotify() {
        viewState.value = viewState.value
            .copy(isNotifyInstalled = true)
    }

    fun saveTime() {
        dataLocalDataSource.saveHour(viewState.value.timeForReminder.hour)
        dataLocalDataSource.saveMinute(viewState.value.timeForReminder.minute)
    }

    fun resetTime() {
        dataLocalDataSource.saveHour("")
        dataLocalDataSource.saveMinute("")
    }

    private fun loadMinutes() = dataLocalDataSource.loadMinutes()

    private fun loadHours() = dataLocalDataSource.loadHours()

    fun cancelNotification() {
        viewState.value = viewState.value
            .copy(isNotifyInstalled = false)
    }

    fun setHours(hour: String) {
        val timeForReminder = viewState.value.timeForReminder.copy(hour = hour)
        viewState.value = viewState.value
            .copy(timeForReminder = timeForReminder)
    }

    fun setMinutes(minute: String) {
        val timeForReminder = viewState.value.timeForReminder.copy(minute = minute)
        viewState.value = viewState.value
            .copy(timeForReminder = timeForReminder)
    }
}