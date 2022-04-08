package com.fiz.mono.ui.report.category

import android.graphics.Canvas
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.data_source.TransactionDataSource
import com.fiz.mono.data.repositories.CategoryRepository
import com.fiz.mono.ui.report.select.SelectCategoryFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    fun clickMonthToggleButton() {
        if (uiState.value.reportFor == ReportCategoryUiState.MONTH) return
        _uiState.update {
            it.copy(reportFor = ReportCategoryUiState.MONTH)
        }
    }

    fun clickWeekToggleButton() {
        if (uiState.value.reportFor == ReportCategoryUiState.WEEK) return
        _uiState.update {
            it.copy(reportFor = ReportCategoryUiState.WEEK)
        }
    }

    fun onGraphImageViewLayoutChange() {
        _uiState.update {
            it.copy(isCanGraph = true)
        }
    }

    fun init(type: String, id: String) {
        _uiState.update {
            it.copy(
                isExpense = type == SelectCategoryFragment.TYPE_EXPENSE,
                id = id
            )
        }
    }

    fun drawGraph(
        canvas: Canvas,
        width: Int,
        height: Int,
        color: Int,
        density: Float,
        textSize: Float,
        colorText: Int,
        colorForShader1: Int,
        colorForShader2: Int
    ) {
        if (uiState.value.reportFor == ReportCategoryUiState.MONTH) {
            uiState.value.drawLineMonth(
                canvas,
                width,
                height,
                color,
                density,
                colorForShader1,
                colorForShader2
            )
            uiState.value.drawTextMonth(canvas, width, height, textSize, colorText)
        } else {
            uiState.value.drawLineWeek(
                canvas,
                width,
                height,
                color,
                density,
                colorForShader1,
                colorForShader2
            )
            uiState.value.drawTextWeek(canvas, width, height, textSize, colorText)
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