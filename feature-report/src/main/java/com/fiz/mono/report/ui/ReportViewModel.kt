package com.fiz.mono.report.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.common.ui.resources.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor() : ViewModel() {
    var viewState = MutableStateFlow(ReportViewState())
        private set

    var viewEffects = MutableSharedFlow<ReportViewEffect>()
        private set

    fun getTextTypeReport() = if (viewState.value.categorySelectedReport == CategoryReport.Monthly)
        R.string.month_report
    else
        R.string.category_report

    fun onEvent(event: ReportEvent) {
        when (event) {
            ReportEvent.CategoryClicked -> categoryClicked()
            ReportEvent.MonthlyClicked -> monthlyClicked()
            ReportEvent.DateTextClicked -> dateTextClicked()
        }
    }

    private fun dateTextClicked() {
        viewModelScope.launch {
            viewEffects.emit(ReportViewEffect.MoveCalendar)
        }
    }

    private fun monthlyClicked() {
        viewModelScope.launch {
            if (clickMonthly())
                viewEffects.emit(ReportViewEffect.MoveReturn)
        }
    }

    private fun clickMonthly(): Boolean {
        if (viewState.value.categorySelectedReport == CategoryReport.Monthly)
            return false

        viewState.value = viewState.value
            .copy(categorySelectedReport = CategoryReport.Monthly)
        return true
    }

    private fun categoryClicked() {
        viewModelScope.launch {
            if (clickCategory()) {
                viewEffects.emit(ReportViewEffect.MoveSelectCategory)
            }
        }
    }

    private fun clickCategory(): Boolean {
        if (viewState.value.categorySelectedReport == CategoryReport.Category)
            return false

        viewState.value = viewState.value
            .copy(categorySelectedReport = CategoryReport.Category)
        return true
    }

}

