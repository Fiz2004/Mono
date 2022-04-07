package com.fiz.mono.ui.report.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.data_source.TransactionDataSource
import com.fiz.mono.data.repositories.CategoryRepository
import com.fiz.mono.ui.models.CategoryUiState
import com.fiz.mono.ui.models.TransactionUiState
import com.fiz.mono.ui.shared_adapters.InfoDay
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

data class ReportCategoryUiState(
    val allCategoryExpense: List<CategoryUiState> = listOf(),
    val allCategoryIncome: List<CategoryUiState> = listOf(),
    val allTransactions: List<TransactionUiState> = listOf(),
    val reportFor: Int = ReportCategoryFragment.MONTH,
    val isCanGraph: Boolean = false
)

class ReportCategoryViewModel(
    private val categoryRepository: CategoryRepository,
    transactionDataSource: TransactionDataSource
) : ViewModel() {
    private var _uiState = MutableStateFlow(ReportCategoryUiState())
    val uiState: StateFlow<ReportCategoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategoryExpenseForInput().collect { allCategoryExpense ->
                _uiState.update {
                    it.copy(
                        allCategoryExpense = allCategoryExpense
                    )
                }
            }
        }
        viewModelScope.launch {
            categoryRepository.getAllCategoryIncomeForInput().collect { allCategoryIncome ->
                _uiState.update {
                    it.copy(
                        allCategoryIncome = allCategoryIncome
                    )
                }
            }
        }
        viewModelScope.launch {
            transactionDataSource.allTransactions.collect { allTransactions ->
                _uiState.update {
                    it.copy(
                        allTransactions = allTransactions
                    )
                }
            }
        }
    }

    fun getTransactions(
        tabSelectedReport: Int,
        date: Calendar,
        nameCategory: String
    ): MutableList<TransactionsDataItem> {
        var groupTransactions =
            uiState.value.allTransactions.sortedByDescending { it.date.time }.groupBy {
                SimpleDateFormat(
                    "MMM dd, yyyy",
                    Locale.getDefault()
                ).format(it.date.time)
            }

        groupTransactions = groupTransactions.mapValues {
            it.value.filter { it.nameCategory == nameCategory }
        }.filterValues { it.isNotEmpty() }

        val items = mutableListOf<TransactionsDataItem>()
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
        if (uiState.value.reportFor == ReportCategoryFragment.MONTH) return
        _uiState.update {
            it.copy(reportFor = ReportCategoryFragment.MONTH)
        }
    }

    fun clickWeekToggleButton() {
        if (uiState.value.reportFor == ReportCategoryFragment.WEEK) return
        _uiState.update {
            it.copy(reportFor = ReportCategoryFragment.WEEK)
        }
    }

    fun onGraphImageViewLayoutChange() {
        _uiState.update {
            it.copy(isCanGraph = true)
        }
    }

}

class ReportCategoryViewModelFactory(
    private val categoryRepository: CategoryRepository,
    private val transactionDataSource: TransactionDataSource
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportCategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportCategoryViewModel(categoryRepository, transactionDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}