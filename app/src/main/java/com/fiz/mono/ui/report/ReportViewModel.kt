package com.fiz.mono.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReportViewModel : ViewModel() {
    private var _categorySelectedReport = MutableLiveData(ReportFragment.MONTHLY)
    val categorySelectedReport: LiveData<Int>
        get() = _categorySelectedReport

    fun clickMonthly() {
        _categorySelectedReport.value = ReportFragment.MONTHLY
    }

    fun clickCategory() {
        _categorySelectedReport.value = ReportFragment.CATEGORY
    }
}
