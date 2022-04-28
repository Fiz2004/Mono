package com.fiz.mono.report.ui.monthly

sealed class ReportMonthlyUiEvent {
    object ClickDateLeft : ReportMonthlyUiEvent()
    object ClickDateRight : ReportMonthlyUiEvent()
    data class ClickTransactionsFilter(val filter: Int) : ReportMonthlyUiEvent()
}