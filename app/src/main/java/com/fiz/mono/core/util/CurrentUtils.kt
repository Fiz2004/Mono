package com.fiz.mono.core.util

import kotlin.math.abs

object CurrentUtils {
    fun getCurrencyFormat(currency: String, value: Double, isPrefixPlus: Boolean): String {
        val prefixPlus = if (isPrefixPlus && value != 0.0) "+" else ""
        return if (value < 0)
            "-" + currency + "%.2f".format(abs(value))
        else
            prefixPlus + currency + "%.2f".format(value)
    }
}