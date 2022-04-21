package com.fiz.mono.core.ui.report.category

import android.graphics.Canvas
import android.graphics.Paint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.core.data.data_source.TransactionDataSource
import com.fiz.mono.core.domain.repositories.CategoryRepository
import com.fiz.mono.core.ui.report.select.SelectCategoryFragment
import com.fiz.mono.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
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
                    when (allCategoryExpense) {
                        is Resource.Loading ->
                            it.copy(
                                allCategoryExpense = listOf()
                            )
                        is Resource.Error ->
                            it.copy(
                                allCategoryExpense = listOf()
                            )
                        is Resource.Success ->
                            it.copy(
                                allCategoryExpense = allCategoryExpense.data!!
                            )
                    }
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

        val paintFont = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.textSize = textSize
            this.color = colorText
            this.textAlign = Paint.Align.CENTER
        }

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

            val dateFormatTextMonth = DateTimeFormatter.ofPattern("LLL")
            val nameMonth: MutableList<String> = mutableListOf()
            for (n in 5 downTo 1) {
                nameMonth.add(dateFormatTextMonth.format(LocalDate.now().minusMonths(n.toLong())))
            }

            uiState.value.drawText(canvas, width, height, paintFont, nameMonth)
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

            val dateFormatTextWeek = DateTimeFormatter.ofPattern("EE")
            val nameDays: MutableList<String> = mutableListOf()
            for (n in 7 downTo 1) {
                nameDays.add(dateFormatTextWeek.format(LocalDate.now().minusDays(n.toLong())))
            }

            uiState.value.drawText(canvas, width, height, paintFont, nameDays)
        }
    }
}