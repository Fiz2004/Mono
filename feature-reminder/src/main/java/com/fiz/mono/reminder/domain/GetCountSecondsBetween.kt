package com.fiz.mono.reminder.domain

import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit

fun getCountSecondsBetween(now: LocalTime, needTime: LocalTime): Int {
    if (now.hour > needTime.hour &&
        now.minute > needTime.minute
    )
        needTime.minusHours(24)

    val seconds = ChronoUnit.SECONDS.between(now, needTime).toInt()

    return if (seconds < 0)
        ChronoUnit.DAYS.duration.seconds.toInt() + seconds
    else
        seconds
}