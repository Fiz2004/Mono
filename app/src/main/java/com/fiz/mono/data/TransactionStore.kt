package com.fiz.mono.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.fiz.mono.data.database.TransactionItemDAO
import com.fiz.mono.ui.calendar.TransactionsDay
import java.text.SimpleDateFormat
import java.util.*

class TransactionStore(private val transactionItemDao: TransactionItemDAO) {
    var allTransactions: LiveData<List<TransactionItem>> = transactionItemDao.getAll()

    fun getAllTransactionsForInput(): LiveData<List<TransactionItem>> {
        return Transformations.map(allTransactions) {
            val result = emptyList<TransactionItem>().toMutableList()
            result.addAll(it)
            result
        }
    }

    fun getGroupTransactionsByDays(date: Calendar, pattern: String) =
        getAllTransactionsForMonth(date)?.groupBy {
            SimpleDateFormat(
                pattern,
                Locale.US
            ).format(it.date.time)
        }

    fun getTransactionDayForCurrentMonthByDays(
        date: Calendar
    ): MutableList<TransactionsDay> {
        val currentDay = date.get(Calendar.DATE)
        val dayOfMonth = date.getActualMaximum(Calendar.DAY_OF_MONTH)
        val groupTransactions = getGroupTransactionsByDays(date, "dd")

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


    fun getAllTransactionsForMonth(
        date: Calendar
    ): List<TransactionItem>? {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)

        val allTransactionsForYear =
            allTransactions.value?.filter {
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

    fun getAllTransactionsForDay(
        date: Calendar
    ): List<TransactionItem>? {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)
        val currentDay = date.get(Calendar.DATE)

        val allTransactionsForMonth =
            getAllTransactionsForMonth(date)

        val result = allTransactionsForMonth?.filter {
            SimpleDateFormat(
                "dd",
                Locale.US
            ).format(it.date.time) == currentDay.toString()
        }

        return result
    }

    suspend fun insertNewTransaction(newTransaction: TransactionItem) {
        transactionItemDao.insert(newTransaction)
    }
}