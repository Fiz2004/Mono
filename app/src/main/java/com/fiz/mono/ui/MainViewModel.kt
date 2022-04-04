package com.fiz.mono.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    private var _date = MutableLiveData(Calendar.getInstance())
    val date: LiveData<Calendar> = _date

    fun getFormatDate(pattern: String): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date.value?.time ?: "")
    }

    fun setMonth(month: Int) {
        _date.value?.set(Calendar.MONTH, month)
        _date.value = date.value
    }

    fun setDate(day: Int) {
        if (day == 0) return

        _date.value?.set(Calendar.DATE, day)
        _date.value = date.value
    }

    fun dateDayPlusOne() {
        _date.value?.add(Calendar.DAY_OF_YEAR, 1)
        _date.value = date.value
    }

    fun dateDayMinusOne() {
        _date.value?.add(Calendar.DAY_OF_YEAR, -1)
        _date.value = date.value
    }

    fun dateMonthPlusOne() {
        _date.value?.add(Calendar.MONTH, 1)
        _date.value = date.value
    }

    fun dateMonthMinusOne() {
        _date.value?.add(Calendar.MONTH, -1)
        _date.value = date.value
    }
}