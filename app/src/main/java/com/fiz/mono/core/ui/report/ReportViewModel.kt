package com.fiz.mono.core.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor() : ViewModel() {
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
