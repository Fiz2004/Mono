package com.fiz.mono.calendar.ui

import com.fiz.mono.core.util.TimeUtils
import com.fiz.mono.domain.models.Transaction
import com.fiz.mono.ui.calendar.TransactionsDay
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

data class CalendarUiState(
    val date: LocalDate = LocalDate.now(),
    val isDateChange: Boolean = true,
    val isAllTransactionsLoaded: Boolean = false,
    val allTransactions: List<Transaction> = listOf(),
) {
    val calendarDataItem: List<CalendarDataItem>
        get() = CalendarDataItem.getListCalendarDataItem(
            getTransactionsForDaysCurrentMonth(date)
        )
    val transactionsDataItem: List<TransactionsDataItem>
        get() = TransactionsDataItem.getListTransactionsDataItem(
            getAllTransactionsForDay(date)
        )

    private val dateFormatDay = DateTimeFormatter.ofPattern("dd")

    private fun getTransactionsForDaysCurrentMonth(date: LocalDate): List<TransactionsDay> {
        val result = emptyList<TransactionsDay>().toMutableList()
        result.addAll(getEmptyTransactionDayBeforeCurrentMonth(date))
        val transactionDayForCurrentMonthByDays =
            getTransactionDayForCurrentMonthByDays(date)
        result.addAll(transactionDayForCurrentMonthByDays)
        result.addAll(getEmptyTransactionDayAfterCurrentMonth(date))
        return result
    }

    private fun getEmptyTransactionDayBeforeCurrentMonth(
        date: LocalDate
    ): MutableList<TransactionsDay> {
        val numberFirstDayOfWeek = TimeUtils.getNumberFirstDayOfWeek(date)
        return TransactionsDay.getListEmptyTransactionDay(numberFirstDayOfWeek - 1)
    }

    private fun getEmptyTransactionDayAfterCurrentMonth(
        date: LocalDate
    ): MutableList<TransactionsDay> {
        val dayOfWeekLastDay = TimeUtils.getNumberLastDayOfWeek(date)
        return TransactionsDay.getListEmptyTransactionDay(7 - dayOfWeekLastDay)
    }

    private fun getTransactionDayForCurrentMonthByDays(
        date: LocalDate
    ): MutableList<TransactionsDay> {
        val dayOfMonth = date.lengthOfMonth()
        val groupTransactions = getGroupTransactionsByDays(date)

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

    private fun getGroupTransactionsByDays(date: LocalDate) =
        getAllTransactionsForMonth(date).groupBy {
            dateFormatDay.format(it.localDate)
        }

    private fun getAllTransactionsForMonth(
        date: LocalDate
    ): List<Transaction> {
        return allTransactions.filter { it.localDate.year == date.year }
            .filter { it.localDate.month == date.month }
    }

    private fun getAllTransactionsForDay(
        date: LocalDate
    ): List<Transaction> {
        val allTransactionsForMonth =
            getAllTransactionsForMonth(date)

        val result = allTransactionsForMonth.filter {
            it.localDate.dayOfMonth == date.dayOfMonth
        }

        return result
    }
}