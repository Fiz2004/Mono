package com.fiz.mono.domain.use_case

import com.fiz.mono.domain.models.TransactionsDay
import com.fiz.mono.util.TimeUtils
import org.threeten.bp.LocalDate

fun getEmptyTransactionDayBeforeCurrentMonth(
    date: LocalDate
): MutableList<TransactionsDay> {
    val numberFirstDayOfWeek = TimeUtils.getNumberFirstDayOfWeek(date)
    return TransactionsDay.getListEmptyTransactionDay(numberFirstDayOfWeek - 1)
}