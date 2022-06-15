package com.fiz.mono.reminder.ui

import com.fiz.mono.reminder.domain.models.TimeForReminder

data class ReminderViewState(
    val timeForReminder: TimeForReminder = TimeForReminder(),
    var isNotifyInstalled: Boolean = false,
    val isErrorHourEditText: Boolean = false,
    val isErrorMinuteEditText: Boolean = false,
) {
    val isCanReminder: Boolean
        get() {
            try {
                val hour = timeForReminder.hour.toInt()
                val minute = timeForReminder.minute.toInt()
                if (hour > 23)
                    return false
                if (minute > 59)
                    return false
                return true
            } catch (e: Exception) {
                return false
            }
        }
}
