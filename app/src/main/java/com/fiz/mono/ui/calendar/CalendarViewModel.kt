package com.fiz.mono.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.TransactionStore
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewModel(
    private val categoryStore: CategoryStore,
    private val transactionStore: TransactionStore
) :
    ViewModel() {
    var allTransaction = transactionStore.getAllTransactionsForInput()

    fun getTransactionsOfDays(date: Calendar): List<TransactionsDay> {

        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)
        val currentDay = date.get(Calendar.DATE)
        val dayOfMonth = date.getActualMaximum(Calendar.DAY_OF_MONTH)

        val allTransactionsForMonth =
            getAllTransactionsForMonth(allTransaction.value, currentYear, currentMonth)

        val groupTransactions =
            getGroupTransactionsByDays(allTransactionsForMonth)

        val result = emptyList<TransactionsDay>().toMutableList()
        result.addAll(getEmptyTransactionDayBeforeCurrentMonth(date))
        result.addAll(getTransactionDayForCurrentMonth(dayOfMonth, groupTransactions, currentDay))
        result.addAll(getEmptyTransactionDayAfterCurrentMonth(date))
        return result
    }

    private fun getTransactionDayForCurrentMonth(
        dayOfMonth: Int,
        groupTransactions: Map<String, List<TransactionItem>>?,
        currentDay: Int
    ): MutableList<TransactionsDay> {
        val result = emptyList<TransactionsDay>().toMutableList()
        for (day in 1..dayOfMonth) {
            var expense = false
            var income = false
            val dayString = if (day < 10) "0$day" else "$day"
            if (groupTransactions?.keys?.contains(dayString) == true) {
                income = groupTransactions[dayString]?.any { it.value > 0 } == true
                expense = groupTransactions[dayString]?.any { it.value < 0 } == true
            }
            result.add(TransactionsDay(day, expense, income, day == currentDay))
        }
        return result
    }

    private fun getEmptyTransactionDayAfterCurrentMonth(
        date: Calendar
    ): MutableList<TransactionsDay> {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)
        val currentDay = date.get(Calendar.DATE)
        val dayOfMonth = date.getActualMaximum(Calendar.DAY_OF_MONTH)

        val result = emptyList<TransactionsDay>().toMutableList()
        val dayOfWeekLastDay = getNumberLastDayOfWeek(currentYear, currentMonth, dayOfMonth)
        repeat(7 - dayOfWeekLastDay) {
            result.add(TransactionsDay(day = 0, expense = false, income = false))
        }
        return result
    }

    private fun getEmptyTransactionDayBeforeCurrentMonth(
        date: Calendar
    ): MutableList<TransactionsDay> {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)
        val currentDay = date.get(Calendar.DATE)

        val result = emptyList<TransactionsDay>().toMutableList()
        val numberFirstDayOfWeek = getNumberFirstDayOfWeek(currentYear, currentMonth, currentDay)
        repeat(numberFirstDayOfWeek - 1) {
            result.add(TransactionsDay(day = 0, expense = false, income = false))
        }
        return result
    }

    private fun getGroupTransactionsByDays(allTransactionsForMonth: List<TransactionItem>?) =
        allTransactionsForMonth?.groupBy {
            SimpleDateFormat(
                "dd",
                Locale.US
            ).format(it.date.time)
        }

    private fun getAllTransactionsForMonth(
        AllTransactions: List<TransactionItem>?,
        currentYear: Int,
        currentMonth: Int
    ): List<TransactionItem>? {
        val allTransactionsForYear =
            AllTransactions?.filter {
                SimpleDateFormat(
                    "yyyy",
                    Locale.US
                ).format(it.date.time) == currentYear.toString()
            }
        val allTransactionsForMonth =
            allTransactionsForYear?.filter {
                SimpleDateFormat(
                    "M",
                    Locale.US
                ).format(it.date.time) == (currentMonth + 1).toString()
            }
        return allTransactionsForMonth
    }
}

private fun getNumberLastDayOfWeek(
    currentYear: Int,
    currentMonth: Int,
    dayOfMonth: Int
): Int {
    val dateLastDayOfWeek = Calendar.getInstance()
    dateLastDayOfWeek.set(currentYear, currentMonth, dayOfMonth)

    val numberLastDayOfWeekInLocaleUS = dateLastDayOfWeek.get(Calendar.DAY_OF_WEEK)

    return getNumberDayOfWeek(numberLastDayOfWeekInLocaleUS)
}

private fun getNumberFirstDayOfWeek(
    currentYear: Int,
    currentMonth: Int,
    currentDay: Int
): Int {
    val dateFirstDayOfWeek = Calendar.getInstance()
    dateFirstDayOfWeek.set(currentYear, currentMonth, 1)

    val numberFirstDayOfWeekInLocaleUS = dateFirstDayOfWeek.get(Calendar.DAY_OF_WEEK)
    return getNumberDayOfWeek(numberFirstDayOfWeekInLocaleUS)
}

private fun getNumberDayOfWeek(numberDayOfWeekInLocaleUS: Int): Int =
    if ((numberDayOfWeekInLocaleUS - 1) == 0)
        7
    else
        numberDayOfWeekInLocaleUS - 1


class CalendarViewModelFactory(
    private val categoryStore: CategoryStore,
    private val transactionStore: TransactionStore
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(categoryStore, transactionStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}