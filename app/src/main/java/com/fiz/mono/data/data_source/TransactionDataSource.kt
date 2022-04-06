package com.fiz.mono.data.data_source

import com.fiz.mono.data.database.dao.TransactionDao
import com.fiz.mono.data.entity.Transaction
import com.fiz.mono.ui.calendar.TransactionsDay
import com.fiz.mono.util.TimeUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TransactionDataSource(private val transactionDao: TransactionDao) {
    var allTransactions: Flow<List<Transaction>> = transactionDao.getAll()

    suspend fun getGroupTransactionsByDays(date: Calendar, pattern: String) =
        getAllTransactionsForMonth(date)?.groupBy {
            SimpleDateFormat(
                pattern,
                Locale.getDefault()
            ).format(it.date.time)
        }

    private suspend fun getTransactionDayForCurrentMonthByDays(
        date: Calendar
    ): MutableList<TransactionsDay> {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)
        val currentDay = date.get(Calendar.DATE)
        val dayOfMonth = date.getActualMaximum(Calendar.DAY_OF_MONTH)
        val groupTransactions = getGroupTransactionsByDays(date, "dd")

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
            if (groupTransactions?.keys?.contains(dayString) == true) {
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

    suspend fun getAllTransactionsForMonth(
        date: Calendar
    ): List<Transaction>? {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)

        val allTransactionsForYear =
            allTransactions.first().filter {
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

    suspend fun getAllTransactionsForDay(
        date: Calendar
    ): List<Transaction>? {
        val currentDay = date.get(Calendar.DATE)
        val dayString = if (currentDay < 10) "0$currentDay" else "$currentDay"

        val allTransactionsForMonth =
            getAllTransactionsForMonth(date)

        val result = allTransactionsForMonth?.filter {
            SimpleDateFormat(
                "dd",
                Locale.getDefault()
            ).format(it.date.time) == dayString
        }

        return result
    }

    suspend fun insertNewTransaction(newTransaction: Transaction) {
        transactionDao.insert(newTransaction)
    }

    suspend fun deleteAll() {
        allTransactions.first().map {
            it.photo.map path@{
                if (it == null) return@path
                val fdelete = File(it)
                if (fdelete.exists()) {
                    fdelete.delete()
                }
            }
            transactionDao.delete(it)
        }
    }

    suspend fun delete(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    suspend fun getTransactionsForDaysCurrentMonth(date: Calendar): List<TransactionsDay> {
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

    suspend fun getTransactionByID(transaction: Int): Transaction? {
        return if (transaction != -1)
            allTransactions.first().find { it.id == transaction }?.copy()
        else
            null
    }

    suspend fun getNewId(): Int {
        val lastItem = allTransactions.first().lastOrNull()
        val id = lastItem?.id
        return id?.let { it + 1 } ?: 0
    }
}