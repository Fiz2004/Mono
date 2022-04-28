package com.fiz.mono.report.ui.category

import android.graphics.Canvas
import android.graphics.Paint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.repositories.CategoryRepository
import com.fiz.mono.domain.repositories.TransactionRepository
import com.fiz.mono.report.ui.select.SelectCategoryFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ReportCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    transactionRepository: TransactionRepository
) : ViewModel() {
    var uiState = MutableStateFlow(ReportCategoryUiState()); private set

    init {

        categoryRepository.allCategoryExpense
            .onEach { allCategoryExpense ->
                uiState.value = uiState.value
                    .copy(allCategoryExpense = allCategoryExpense)
            }.launchIn(viewModelScope)

        categoryRepository.allCategoryIncome
            .onEach { allCategoryIncome ->
                uiState.value = uiState.value
                    .copy(allCategoryIncome = allCategoryIncome)
            }.launchIn(viewModelScope)

        transactionRepository.allTransactions
            .onEach { allTransactions ->
                uiState.value = uiState.value
                    .copy(allTransactions = allTransactions)
            }.launchIn(viewModelScope)

    }

    fun clickMonthToggleButton() {
        if (uiState.value.reportFor == ReportCategoryUiState.MONTH) return

        uiState.value = uiState.value
            .copy(reportFor = ReportCategoryUiState.MONTH)

    }

    fun clickWeekToggleButton() {
        if (uiState.value.reportFor == ReportCategoryUiState.WEEK) return

        uiState.value = uiState.value
            .copy(reportFor = ReportCategoryUiState.WEEK)
    }

    fun onGraphImageViewLayoutChange() {
        uiState.value = uiState.value
            .copy(isCanGraph = true)
    }

    fun start(type: String, id: String) {
        uiState.value = uiState.value
            .copy(
                isExpense = type == SelectCategoryFragment.TYPE_EXPENSE,
                id = id
            )
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