package com.fiz.mono.domain.use_case

import com.fiz.mono.core.util.TimeUtils
import com.fiz.mono.domain.models.TransactionsDay
import org.threeten.bp.LocalDate

fun getEmptyTransactionDayAfterCurrentMonth(
    date: LocalDate
): MutableList<TransactionsDay> {
    val dayOfWeekLastDay = TimeUtils.getNumberLastDayOfWeek(date)
    return TransactionsDay.getListEmptyTransactionDay(7 - dayOfWeekLastDay)
}