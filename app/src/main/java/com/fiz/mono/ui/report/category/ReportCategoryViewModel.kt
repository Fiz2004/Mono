package com.fiz.mono.ui.report.category

import androidx.lifecycle.*
import com.fiz.mono.data.data_source.CategoryDataSource
import com.fiz.mono.data.data_source.TransactionDataSource
import com.fiz.mono.ui.models.TransactionUiState
import com.fiz.mono.ui.shared_adapters.InfoDay
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class ReportCategoryViewModel(
    categoryDataSource: CategoryDataSource,
    transactionDataSource: TransactionDataSource
) : ViewModel() {
    var allCategoryExpense = categoryDataSource.allCategoryExpense
    var allCategoryIncome = categoryDataSource.allCategoryIncome

    val allTransactions = transactionDataSource.allTransactions.asLiveData()

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
        transactions: List<TransactionUiState>,
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
    private val categoryDataSource: CategoryDataSource,
    private val transactionDataSource: TransactionDataSource
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportCategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportCategoryViewModel(categoryDataSource, transactionDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}