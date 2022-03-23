package com.fiz.mono.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class MainViewModel : ViewModel() {
    var firstTime: Boolean = false

    var currency: String = "$"

    var PIN: String = ""

    var statePIN = ""

    var date: MutableLiveData<Calendar> = MutableLiveData(Calendar.getInstance())

    var tabSelectedReport: Int = 0

    var categorySelectedReport = 0

    fun getFormatDate(pattern: String): String {
        return SimpleDateFormat(pattern, Locale.US).format(date.value?.time ?: "")
    }

    fun setMonth(month: Int) {
        date.value?.set(Calendar.MONTH, month)
        date.value = date.value
    }

    fun setDate(day: Int) {
        date.value?.set(Calendar.DATE, day)
        date.value = date.value
    }

    fun dateDayPlusOne() {
        date.value?.add(Calendar.DAY_OF_YEAR, 1)
        date.value = date.value
    }

    fun dateDayMinusOned() {
        date.value?.add(Calendar.DAY_OF_YEAR, -1)
        date.value = date.value
    }

    fun dateMonthPlusOne() {
        date.value?.add(Calendar.MONTH, 1)
        date.value = date.value
    }

    fun dateMonthMinusOne() {
        date.value?.add(Calendar.MONTH, -1)
        date.value = date.value
    }
}

fun getCurrencyFormat(currency: String, value: Double, isPrefixPlus: Boolean): String {
    val prefixPlus = if (isPrefixPlus && value != 0.0) "+" else ""
    return if (value < 0)
        "-" + currency + "%.2f".format(abs(value))
    else
        prefixPlus + currency + "%.2f".format(value)
}