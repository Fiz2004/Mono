package com.fiz.mono.reminder.ui

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.reminder.domain.DataLocalDataSource
import com.fiz.mono.reminder.domain.getCountSecondsBetween
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime
import javax.inject.Inject

sealed class ReminderEvent {
    object ActivityCreated : ReminderEvent()
    object BackButtonClicked : ReminderEvent()
    object SetReminderButtonClicked : ReminderEvent()
    data class HoursEditTextChanged(val value: String) : ReminderEvent()
    data class MinutesEditTextChanged(val value: String) : ReminderEvent()
}

sealed class ReminderViewEffect {
    object MoveReturn : ReminderViewEffect()
}

@HiltViewModel
class ReminderViewModel @Inject constructor(private val dataLocalDataSource: DataLocalDataSource) :
    ViewModel() {

    var viewState = MutableStateFlow(ReminderViewState())
        private set

    var viewEffects = MutableSharedFlow<ReminderViewEffect>()
        private set

    fun onEvent(event: ReminderEvent) {
        when (event) {
            ReminderEvent.ActivityCreated -> activityCreated()
            ReminderEvent.BackButtonClicked -> backButtonClicked()
            is ReminderEvent.HoursEditTextChanged -> hoursEditTextChanged(event.value)
            is ReminderEvent.MinutesEditTextChanged -> minutesEditTextChanged(event.value)
            ReminderEvent.SetReminderButtonClicked -> setReminderButtonClicked()
        }
    }

    private fun activityCreated() {
        val hours = dataLocalDataSource.loadHours()
        val minutes = dataLocalDataSource.loadMinutes()
        hoursEditTextChanged(hours)
        minutesEditTextChanged(minutes)
        if (viewState.value.timeForReminder.isNotEmpty()) {
            viewState.value = viewState.value
                .copy(isNotifyInstalled = true)
        }
    }

    private fun hoursEditTextChanged(value: String) {
        val timeForReminder = viewState.value.timeForReminder.copy(hour = value)
        viewState.value = viewState.value
            .copy(timeForReminder = timeForReminder)
    }

    private fun minutesEditTextChanged(value: String) {
        val timeForReminder = viewState.value.timeForReminder.copy(minute = value)
        viewState.value = viewState.value
            .copy(timeForReminder = timeForReminder)
    }

    private fun backButtonClicked() {
        viewModelScope.launch {
            viewEffects.emit(ReminderViewEffect.MoveReturn)
        }
    }

    private fun setReminderButtonClicked() {
        viewModelScope.launch {
            viewState.value = viewState.value
                .copy(isNotifyInstalled = !viewState.value.isNotifyInstalled)

            if (viewState.value.isNotifyInstalled) {
                saveTime()
                viewEffects.emit(ReminderViewEffect.MoveReturn)
            } else {
                resetTime()
            }
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

    private fun saveTime() {
        dataLocalDataSource.saveHour(viewState.value.timeForReminder.hour)
        dataLocalDataSource.saveMinute(viewState.value.timeForReminder.minute)
    }

    private fun resetTime() {
        dataLocalDataSource.saveHour("")
        dataLocalDataSource.saveMinute("")
    }
}