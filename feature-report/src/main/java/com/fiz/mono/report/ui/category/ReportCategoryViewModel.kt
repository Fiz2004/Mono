package com.fiz.mono.report.ui.category

import android.graphics.Canvas
import android.graphics.Paint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.models.TypeTransaction
import com.fiz.mono.domain.repositories.CategoryRepository
import com.fiz.mono.domain.repositories.SettingsRepository
import com.fiz.mono.domain.repositories.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ReportCategoryViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
    categoryRepository: CategoryRepository,
    transactionRepository: TransactionRepository
) : ViewModel() {
    var viewState = MutableStateFlow(ReportCategoryViewState())
        private set

    init {

        settingsRepository.currency.load()
            .onEach { currency ->
                viewState.value = viewState.value
                    .copy(currency = currency)
            }.launchIn(viewModelScope)

    }

    init {

        categoryRepository.observeCategoriesExpense
            .onEach { allCategoryExpense ->
                viewState.value = viewState.value
                    .copy(allCategoryExpense = allCategoryExpense)
            }.launchIn(viewModelScope)

        categoryRepository.observeCategoriesIncome
            .onEach { allCategoryIncome ->
                viewState.value = viewState.value
                    .copy(allCategoryIncome = allCategoryIncome)
            }.launchIn(viewModelScope)

        transactionRepository.allTransactions
            .onEach { allTransactions ->
                viewState.value = viewState.value
                    .copy(allTransactions = allTransactions)
            }.launchIn(viewModelScope)

    }

    fun clickMonthToggleButton() {
        if (viewState.value.reportFor == PeriodForReport.Month) return

        viewState.value = viewState.value
            .copy(reportFor = PeriodForReport.Month)

    }

    fun clickWeekToggleButton() {
        if (viewState.value.reportFor == PeriodForReport.Week) return

        viewState.value = viewState.value
            .copy(reportFor = PeriodForReport.Week)
    }

    fun onGraphImageViewLayoutChange() {
        viewState.value = viewState.value
            .copy(isCanGraph = true)
    }

    fun start(type: TypeTransaction, id: String) {
        viewState.value = viewState.value
            .copy(
                isExpense = type == TypeTransaction.Expense,
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

        if (viewState.value.reportFor == PeriodForReport.Month) {
            val valuesByMonth =
                viewState.value.getValuesForVerticalForMonth()
                    .map { usefulHeight - usefulHeight * it }
            viewState.value.drawLine(
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

            viewState.value.drawText(canvas, width, height, paintFont, nameMonth)
        } else {
            val valuesByWeek =
                viewState.value.getValuesForVerticalForWeek()
                    .map { usefulHeight - usefulHeight * it }
            viewState.value.drawLine(
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

            viewState.value.drawText(canvas, width, height, paintFont, nameDays)
        }
    }
}