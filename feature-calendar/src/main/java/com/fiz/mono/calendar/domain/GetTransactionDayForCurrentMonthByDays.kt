package com.fiz.mono.calendar.domain

import com.fiz.mono.domain.models.Transaction
import com.fiz.mono.domain.models.TransactionsDay
import org.threeten.bp.LocalDate
import java.util.*

fun getTransactionDayForCurrentMonthByDays(
    allTransactions: List<Transaction>, date: LocalDate
): MutableList<TransactionsDay> {
    val dayOfMonth = date.lengthOfMonth()
    val groupTransactions = getGroupTransactionsByDays(allTransactions, date)

    val today = Calendar.getInstance()
    val todayYear = today.get(Calendar.YEAR)
    val todayMonth = today.get(Calendar.MONTH)
    val todayDay = today.get(Calendar.DATE)
    val isToday = date.year == todayYear && date.monthValue == todayMonth

    val result = emptyList<TransactionsDay>().toMutableList()
    for (day in 1..dayOfMonth) {
        var expense = false
        var income = false
        val dayString = if (day < 10) "0$day" else "$day"
        if (groupTransactions.keys.contains(dayString)) {
            income = groupTransactions[dayString]?.any { it.value > 0 } == true
            expense = groupTransactions[dayString]?.any { it.value < 0 } == true
        }
        result.add(
            TransactionsDay(
                day,
                expense,
                income,
                day == date.dayOfMonth,
                isToday && day == todayDay
            )
        )
    }
    return result
}