package com.fiz.mono.ui.report.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.ui.shared_adapters.InfoDay
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class ReportCategoryViewModel(
    categoryStore: CategoryStore,
    transactionStore: TransactionStore
) : ViewModel() {
    var allCategoryExpense = categoryStore.getAllCategoryExpenseForInput()
    var allCategoryIncome = categoryStore.getAllCategoryIncomeForInput()

    val allTransactions = transactionStore.allTransactions

    private val _reportFor = MutableLiveData(ReportCategoryFragment.MONTH)
    val reportFor: LiveData<Int>
        get() = _reportFor

    fun getTransactions(
        tabSelectedReport: Int,
        date: Calendar,
        nameCategory: String
    ): MutableList<TransactionsDataItem> {
        var groupTransactions =
            allTransactions.value?.sortedByDescending { it.date.time }?.groupBy {
                SimpleDateFormat(
                    "MMM dd, yyyy",
                    Locale.getDefault()
                ).format(it.date.time)
            }

        groupTransactions = groupTransactions?.mapValues {
            it.value.filter { it.nameCategory == nameCategory }
        }?.filterValues { it.isNotEmpty() }

        val items = mutableListOf<TransactionsDataItem>()
        if (groupTransactions != null) {
            for (transactionsForDay in groupTransactions) {
                val expense =
                    transactionsForDay.value.filter { it.value < 0 }.map { it.value }
                        .fold(0.0) { acc, d -> acc + d }
                val income =
                    transactionsForDay.value.filter { it.value > 0 }.map { it.value }
                        .fold(0.0) { acc, d -> acc + d }
                items += TransactionsDataItem.InfoDayHeaderItem(
                    InfoDay(
                        transactionsForDay.key,
                        expense,
                        income
                    )
                )
                items += transactionsForDay.value.map { TransactionsDataItem.InfoTransactionItem(it) }
            }
        }
        return items
    }

    fun getValueReportCategory(
        transactions: List<TransactionItem>,
        currency: String,
        isExpense: Boolean,
        categoryName: String
    ): String {
        return if (!isExpense) {
            "+"
        } else {
            ""
        } +
                currency +
                abs(transactions
                    .filter { it.nameCategory == categoryName }
                    .map { it.value }
                    .fold(0.0) { acc, d -> acc + d })
                    .toString()
    }

    fun clickMonthToggleButton() {
        if (_reportFor.value == ReportCategoryFragment.MONTH) return
        _reportFor.value = ReportCategoryFragment.MONTH
    }

    fun clickWeekToggleButton() {
        if (_reportFor.value == ReportCategoryFragment.WEEK) return
        _reportFor.value = ReportCategoryFragment.WEEK
    }

}

class ReportCategoryViewModelFactory(
    private val categoryStore: CategoryStore,
    private val transactionStore: TransactionStore
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportCategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportCategoryViewModel(categoryStore, transactionStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}