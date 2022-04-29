package com.fiz.mono.report.ui

import androidx.lifecycle.ViewModel
import com.fiz.mono.common.ui.resources.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor() : ViewModel() {
    var uiState = MutableStateFlow(ReportUiState()); private set

    fun clickMonthly(): Boolean {
        if (uiState.value.categorySelectedReport == MONTHLY)
            return false

        uiState.value = uiState.value
            .copy(categorySelectedReport = MONTHLY)
        return true
    }

    fun clickCategory(): Boolean {
        if (uiState.value.categorySelectedReport == CATEGORY)
            return false

        uiState.value = uiState.value
            .copy(categorySelectedReport = CATEGORY)
        return true
    }

    fun getTextTypeReport() = if (uiState.value.categorySelectedReport == MONTHLY)
        R.string.month_report
    else
        R.string.category_report

    companion object {
        const val MONTHLY = 0
        const val CATEGORY = 1
    }
}
