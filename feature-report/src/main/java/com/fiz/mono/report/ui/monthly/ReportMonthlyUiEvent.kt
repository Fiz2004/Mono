package com.fiz.mono.report.ui.monthly

import org.threeten.bp.LocalDate

sealed class ReportMonthlyUiEvent {
    data class ClickTransactionsFilter(val filter: Int) : ReportMonthlyUiEvent()
    data class ObserveData(val date: LocalDate) : ReportMonthlyUiEvent()
}