package com.fiz.mono.report.ui

sealed class ReportEvent {
    object MonthlyClicked : ReportEvent()
    object CategoryClicked : ReportEvent()
    object DateTextClicked : ReportEvent()
}