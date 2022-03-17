package com.fiz.mono.ui

import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.math.abs

class MainViewModel : ViewModel() {
    // TODO поставить true перед выпуском, сейчас false для отладки
    var firstTime: Boolean = false
    var log: Boolean = false

    var currency: String = "$"

    var PIN: String = ""

    var statePIN = ""

    var date: Calendar = Calendar.getInstance()

    var tabSelectedReport: Int = 0

    var categorySelectedReport = 0
}

fun getCurrencyFormat(currency: String, value: Double, isPrefixPlus: Boolean): String {
    val prefixPlus = if (isPrefixPlus && value != 0.0) "+" else ""
    return if (value < 0)
        "-" + currency + "%.2f".format(abs(value))
    else
        prefixPlus + currency + "%.2f".format(value)
}