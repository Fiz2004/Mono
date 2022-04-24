package com.fiz.mono.core.ui.calendar

data class TransactionsDay(
    val day: Int,
    val expense: Boolean, val income: Boolean,
    val selected: Boolean = false,
    val today: Boolean = false
) {
    constructor() : this(day = 0, expense = false, income = false, today = false)

    fun getFormatDayOfMonthOrBlank() =
        if (day == 0)
            ""
        else
            day.toString()

    companion object {
        fun getListEmptyTransactionDay(
            times: Int
        ): MutableList<TransactionsDay> {
            val result = emptyList<TransactionsDay>().toMutableList()
            repeat(times) {
                result.add(TransactionsDay())
            }
            return result
        }
    }
}