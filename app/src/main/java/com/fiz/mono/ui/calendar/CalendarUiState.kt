package com.fiz.mono.ui.calendar

import com.fiz.mono.ui.models.TransactionUiState
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import com.fiz.mono.util.TimeUtils
import java.text.SimpleDateFormat
import java.util.*

data class CalendarUiState(
    val date: Calendar = Calendar.getInstance(),
    val isDateChange: Boolean = true,
    val allTransactions: List<TransactionUiState> = listOf(),
    val isReturn: Boolean = false,
) {
    val calendarDataItem: List<CalendarDataItem>
        get() = CalendarDataItem.getListCalendarDataItem(
            getTransactionsForDaysCurrentMonth(date)
        )
    val transactionsDataItem: List<TransactionsDataItem>
        get() = TransactionsDataItem.getListTransactionsDataItem(
            getAllTransactionsForDay(date)
        )

    private fun getTransactionsForDaysCurrentMonth(date: Calendar): List<TransactionsDay> {
        val result = emptyList<TransactionsDay>().toMutableList()
        result.addAll(getEmptyTransactionDayBeforeCurrentMonth(date))
        val transactionDayForCurrentMonthByDays =
            getTransactionDayForCurrentMonthByDays(date)
        result.addAll(transactionDayForCurrentMonthByDays)
        result.addAll(getEmptyTransactionDayAfterCurrentMonth(date))
        return result
    }

    private fun getEmptyTransactionDayBeforeCurrentMonth(
        date: Calendar
    ): MutableList<TransactionsDay> {
        val numberFirstDayOfWeek = TimeUtils.getNumberFirstDayOfWeek(date)
        return TransactionsDay.getListEmptyTransactionDay(numberFirstDayOfWeek - 1)
    }

    private fun getEmptyTransactionDayAfterCurrentMonth(
        date: Calendar
    ): MutableList<TransactionsDay> {
        val dayOfWeekLastDay = TimeUtils.getNumberLastDayOfWeek(date)
        return TransactionsDay.getListEmptyTransactionDay(7 - dayOfWeekLastDay)
    }

    private fun getTransactionDayForCurrentMonthByDays(
        date: Calendar
    ): MutableList<TransactionsDay> {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)
        val currentDay = date.get(Calendar.DATE)
        val dayOfMonth = date.getActualMaximum(Calendar.DAY_OF_MONTH)
        val groupTransactions = getGroupTransactionsByDays(date)

        val today = Calendar.getInstance()
        val todayYear = today.get(Calendar.YEAR)
        val todayMonth = today.get(Calendar.MONTH)
        val todayDay = today.get(Calendar.DATE)
        val isToday = currentYear == todayYear && currentMonth == todayMonth


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
                    day == currentDay,
                    isToday && day == todayDay
                )
            )
        }
        return result
    }

    private fun getGroupTransactionsByDays(date: Calendar) =
        getAllTransactionsForMonth(date).groupBy {
            SimpleDateFormat(
                "dd",
                Locale.getDefault()
            ).format(it.date.time)
        }

    private fun getAllTransactionsForMonth(
        date: Calendar
    ): List<TransactionUiState> {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)

        val allTransactionsForYear =
            allTransactions.filter {
                SimpleDateFormat(
                    "yyyy",
                    Locale.getDefault()
                ).format(it.date.time) == currentYear.toString()
            }
        val allTransactionsForMonth =
            allTransactionsForYear.filter {
                SimpleDateFormat(
                    "M",
                    Locale.getDefault()
                ).format(it.date.time) == (currentMonth + 1).toString()
            }
        return allTransactionsForMonth
    }

    private fun getAllTransactionsForDay(
        date: Calendar
    ): List<TransactionUiState> {
        val currentDay = date.get(Calendar.DATE)
        val dayString = if (currentDay < 10) "0$currentDay" else "$currentDay"

        val allTransactionsForMonth =
            getAllTransactionsForMonth(date)

        val result = allTransactionsForMonth.filter {
            SimpleDateFormat(
                "dd",
                Locale.getDefault()
            ).format(it.date.time) == dayString
        }

        return result
    }
}