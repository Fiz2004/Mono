package com.fiz.mono.ui.report.category

import android.graphics.Canvas
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.data_source.TransactionDataSource
import com.fiz.mono.data.repositories.CategoryRepository
import com.fiz.mono.ui.report.select.SelectCategoryFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    transactionDataSource: TransactionDataSource
) : ViewModel() {
    private var _uiState = MutableStateFlow(ReportCategoryUiState())
    val uiState: StateFlow<ReportCategoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            categoryRepository.getAllCategoryExpenseForInput().collect { allCategoryExpense ->
                _uiState.update {
                    it.copy(
                        allCategoryExpense = allCategoryExpense
                    )
                }
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            categoryRepository.getAllCategoryIncomeForInput().collect { allCategoryIncome ->
                _uiState.update {
                    it.copy(
                        allCategoryIncome = allCategoryIncome
                    )
                }
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
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
        val usefulHeight = height * 0.65f
        if (uiState.value.reportFor == ReportCategoryUiState.MONTH) {
            val valuesByMonth =
                uiState.value.getValuesForVerticalForMonth()
                    .map { usefulHeight - usefulHeight * it }
            uiState.value.drawLine(
                canvas,
                width,
                color,
                density,
                colorForShader1,
                colorForShader2,
                valuesByMonth,
                5f,
                usefulHeight
            )
            uiState.value.drawTextMonth(canvas, width, height, textSize, colorText)
        } else {
            val valuesByWeek =
                uiState.value.getValuesForVerticalForWeek()
                    .map { usefulHeight - usefulHeight * it }
            uiState.value.drawLine(
                canvas,
                width,
                color,
                density,
                colorForShader1,
                colorForShader2,
                valuesByWeek,
                7f,
                usefulHeight
            )
            uiState.value.drawTextWeek(canvas, width, height, textSize, colorText)
        }
    }
}