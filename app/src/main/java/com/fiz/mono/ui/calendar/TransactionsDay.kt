package com.fiz.mono.ui.calendar

data class TransactionsDay(
    val day: Int,
    val expense: Boolean, val income: Boolean,
    val selected: Boolean = false
) {
    fun getFormatDayOfMonthOrBlank() =
        if (day == 0)
            ""
        else
            day.toString()
}