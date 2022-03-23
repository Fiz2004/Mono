package com.fiz.mono.ui.calendar

data class TransactionsDay(
    val day: Int,
    val expense: Boolean, val income: Boolean,
    val selected: Boolean = false
) {
    constructor() : this(day = 0, expense = false, income = false)

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