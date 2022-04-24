package com.fiz.mono.reminder.ui

import com.fiz.mono.reminder.domain.TimeForReminder

data class ReminderUiState(
    val timeForReminder: TimeForReminder = TimeForReminder(),
    var isNotifyInstalled: Boolean = false,
    val isCanReminder: Boolean = true
)