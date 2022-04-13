package com.fiz.mono.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class MainViewModel : ViewModel() {
    var date = MutableLiveData(LocalDate.now())
        private set

    fun getFormatDate(pattern: String): String {
        return DateTimeFormatter.ofPattern(pattern).format(date.value)
    }

    fun setMonth(month: Int) {
        date.value = date.value?.withMonth(month)
    }

    fun setDate(day: Int) {
        if (day == 0) return

        date.value = date.value?.withDayOfMonth(day)
    }

    fun dateDayPlusOne() {
        date.value = date.value?.plusDays(1)
    }

    fun dateDayMinusOne() {
        date.value = date.value?.minusDays(1)
    }

    fun dateMonthPlusOne() {
        date.value = date.value?.plusMonths(1)
    }

    fun dateMonthMinusOne() {
        date.value = date.value?.minusMonths(1)
    }
}