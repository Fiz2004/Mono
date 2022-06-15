package com.fiz.mono.report.ui.select

import com.fiz.mono.domain.models.TypeTransaction

sealed class SelectViewEffect {
    data class MoveCategoryReport(
        val id: String = "",
        val type: TypeTransaction = TypeTransaction.Expense
    ) : SelectViewEffect()
}