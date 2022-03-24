package com.fiz.mono.util

import java.util.*

object TimeUtils {
    fun getNumberLastDayOfWeek(
        date: Calendar
    ): Int {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)
        val dayOfMonth = date.getActualMaximum(Calendar.DAY_OF_MONTH)

        val dateLastDayOfWeek = Calendar.getInstance()
        dateLastDayOfWeek.set(currentYear, currentMonth, dayOfMonth)

        val numberLastDayOfWeekInLocaleUS = dateLastDayOfWeek.get(Calendar.DAY_OF_WEEK)

        return getNumberDayOfWeek(numberLastDayOfWeekInLocaleUS)
    }

    fun getNumberFirstDayOfWeek(
        date: Calendar
    ): Int {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)

        val dateFirstDayOfWeek = Calendar.getInstance()
        dateFirstDayOfWeek.set(currentYear, currentMonth, 1)

        val numberFirstDayOfWeekInLocaleUS = dateFirstDayOfWeek.get(Calendar.DAY_OF_WEEK)
        return getNumberDayOfWeek(numberFirstDayOfWeekInLocaleUS)
    }

    private fun getNumberDayOfWeek(numberDayOfWeekInLocaleUS: Int): Int =
        if ((numberDayOfWeekInLocaleUS - 1) == 0)
            7
        else
            numberDayOfWeekInLocaleUS - 1

    fun getDate(year: Int, month: Int, day: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return calendar.time
    }
}