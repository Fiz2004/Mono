package com.fiz.mono.reminder.ui

import com.fiz.mono.common.ui.resources.R
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


    val textForButtonReminder: Int
        get() = if (isNotifyInstalled) R.string.remove_reminder else R.string.set_reminder

    val hoursErrorEditText: Int?
        get() = if (!isStatusHour(timeForReminder.hour))
            R.string.invalid_number
        else
            null

    val minutesErrorEditText: Int?
        get() = if (!isStatusMinute(timeForReminder.minute))
            R.string.invalid_number
        else
            null
}
