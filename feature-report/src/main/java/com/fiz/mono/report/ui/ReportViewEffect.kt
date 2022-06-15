package com.fiz.mono.report.ui

sealed class ReportViewEffect {
    object MoveSelectCategory : ReportViewEffect()
    object MoveReturn : ReportViewEffect()
    object MoveCalendar : ReportViewEffect()
}