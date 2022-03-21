package com.fiz.mono.ui

import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class MainViewModel : ViewModel() {
    var firstTime: Boolean = false

    var currency: String = "$"

    var PIN: String = ""

    var statePIN = ""

    var date: Calendar = Calendar.getInstance()

    var tabSelectedReport: Int = 0

    var categorySelectedReport = 0

    fun getFormatDate(): String {
        return SimpleDateFormat("MMM dd, yyyy (EEE)").format(date.time)
    }

    fun datePlusOne() {
        date.add(Calendar.DAY_OF_YEAR, 1)
    }

    fun dateMinusOne() {
        date.add(Calendar.DAY_OF_YEAR, -1)
    }
}

fun getCurrencyFormat(currency: String, value: Double, isPrefixPlus: Boolean): String {
    val prefixPlus = if (isPrefixPlus && value != 0.0) "+" else ""
    return if (value < 0)
        "-" + currency + "%.2f".format(abs(value))
    else
        prefixPlus + currency + "%.2f".format(value)
}