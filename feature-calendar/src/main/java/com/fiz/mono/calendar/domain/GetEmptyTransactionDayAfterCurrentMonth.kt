package com.fiz.mono.calendar.domain

import com.fiz.mono.domain.models.TransactionsDay
import com.fiz.mono.util.TimeUtils
import org.threeten.bp.LocalDate

fun getEmptyTransactionDayAfterCurrentMonth(
    date: LocalDate
): MutableList<TransactionsDay> {
    val dayOfWeekLastDay = TimeUtils.getNumberLastDayOfWeek(date)
    return TransactionsDay.getListEmptyTransactionDay(7 - dayOfWeekLastDay)
}