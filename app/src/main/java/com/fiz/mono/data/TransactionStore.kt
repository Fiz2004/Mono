package com.fiz.mono.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.fiz.mono.data.database.TransactionItemDAO
import com.fiz.mono.ui.calendar.TransactionsDay
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TransactionStore(private val transactionItemDao: TransactionItemDAO) {
    var allTransactions: LiveData<List<TransactionItem>> = transactionItemDao.getAll().asLiveData()

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
                Locale.getDefault()
            ).format(it.date.time)
        }

    fun getTransactionDayForCurrentMonthByDays(
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


    fun getAllTransactionsForMonth(
        date: Calendar
    ): List<TransactionItem>? {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)

        val allTransactionsForYear =
            allTransactions.value?.filter {
                SimpleDateFormat(
                    "yyyy",
                    Locale.getDefault()
                ).format(it.date.time) == currentYear.toString()
            }
        val allTransactionsForMonth =
            allTransactionsForYear?.filter {
                SimpleDateFormat(
                    "M",
                    Locale.getDefault()
                ).format(it.date.time) == (currentMonth + 1).toString()
            }
        return allTransactionsForMonth
    }

    fun getAllTransactionsForDay(
        date: Calendar
    ): List<TransactionItem>? {
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

    suspend fun insertNewTransaction(newTransaction: TransactionItem) {
        transactionItemDao.insert(newTransaction)
    }

    suspend fun deleteAll() {
        allTransactions.value?.map {
            it.photo.map path@{
                if (it == null) return@path
                val fdelete = File(it)
                if (fdelete.exists()) {
                    fdelete.delete()
                }
            }
            transactionItemDao.delete(it)
        }
    }

    suspend fun delete(transaction: TransactionItem) {
        transactionItemDao.delete(transaction)
    }

    suspend fun updateTransaction(transactionItem: TransactionItem) {
        transactionItemDao.update(transactionItem)
    }
}