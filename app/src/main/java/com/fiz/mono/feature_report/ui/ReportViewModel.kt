package com.fiz.mono.feature_report.ui

import androidx.lifecycle.ViewModel
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.feature_report.ui.ReportFragment.Companion.MONTHLY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor() : ViewModel() {
    var categorySelectedReport = MutableStateFlow(MONTHLY); private set

    fun clickMonthly(): Boolean {
        if (categorySelectedReport.value == MONTHLY)
            return false

        categorySelectedReport.value = MONTHLY
        return true
    }

    fun clickCategory(): Boolean {
        if (categorySelectedReport.value == ReportFragment.CATEGORY)
            return false

        categorySelectedReport.value = ReportFragment.CATEGORY
        return true
    }

    fun getTextTypeReport() = if (categorySelectedReport.value == MONTHLY)
        R.string.month_report
    else
        R.string.category_report
}
