package com.fiz.mono.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class MainViewModel : ViewModel() {
    private var _date = MutableLiveData(LocalDate.now())
    val date: LiveData<LocalDate> = _date

    fun getFormatDate(pattern: String): String {
        return DateTimeFormatter.ofPattern(pattern).format(date.value)
    }

    fun setMonth(month: Int) {
        _date.value = date.value?.withMonth(month)
    }

    fun setDate(day: Int) {
        if (day == 0) return

        _date.value = date.value?.withDayOfMonth(day)
    }

    fun dateDayPlusOne() {
        _date.value = date.value?.plusDays(1)
    }

    fun dateDayMinusOne() {
        _date.value = date.value?.minusDays(1)
    }

    fun dateMonthPlusOne() {
        _date.value = date.value?.plusMonths(1)
    }

    fun dateMonthMinusOne() {
        _date.value = date.value?.minusMonths(1)
    }
}