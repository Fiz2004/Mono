package com.fiz.mono.report.ui.monthly

sealed class ReportMonthlyEvent {
    object DateLeftClicked : ReportMonthlyEvent()
    object DateRightClicked : ReportMonthlyEvent()
    data class TransactionsFilterClicked(val filter: Int) : ReportMonthlyEvent()
}