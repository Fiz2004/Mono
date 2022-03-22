package com.fiz.mono.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionStore
import java.util.*

class CalendarViewModel(private val categoryStore: CategoryStore, private val transactionStore: TransactionStore) :
    ViewModel() {
    var allTransaction = transactionStore.getAllTransactionsForInput()

    fun getTransactionsOfDays(date: Calendar): List<TransactionsDay> {
        val result = emptyList<TransactionsDay>().toMutableList()
//
//        val currentYear = date.get(Calendar.YEAR)
//        val currentMonth = date.get(Calendar.MONTH) + 1
//        val allTransactionsForYear =
//            allTransaction.value?.filter { SimpleDateFormat("yyyy").format(it.date.time) == currentYear.toString() } as MutableList<TransactionItem>
//        val allTransactionsForMonth =
//            allTransactionsForYear.filter { SimpleDateFormat("M").format(it.date.time) == currentMonth.toString() } as MutableList<TransactionItem>
//
//        val dayOfMonth=date.getActualMaximum(Calendar.DAY_OF_MONTH)
//        val numberDay=date.get(Calendar.DATE)
//        date.add(Calendar.DATE,-(numberDay-1))
//        val dayOfWeekFirstDay=date.get(Calendar.DAY_OF_WEEK)
//        for (n in 1 until dayOfWeekFirstDay)
//            result.add(TransactionsDay(0, false, false))
//        for (day in 1..dayOfMonth) {
//            result.add(TransactionsDay(day, false, false))
//        }
//        date.add(Calendar.DATE,dayOfMonth-1)
//        val dayOfWeekLastDay=date.get(Calendar.DAY_OF_WEEK)
//        for (n in dayOfWeekLastDay..7)
//            result.add(TransactionsDay(0, false, false))

        return listOf(
            TransactionsDay(1, true, true),
            TransactionsDay(2, false, true),
            TransactionsDay(3, true, false),
            TransactionsDay(4, false, false)
        )
    }
}

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