package com.fiz.mono.util

import org.threeten.bp.DateTimeUtils
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
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

    fun getDateMonthYearString(calendar: Calendar?, monthNames: Array<String>): String {
        val fmt = DateTimeFormatter.ofPattern("yyyy")
        val d = DateTimeUtils.toInstant(calendar?.time).atOffset(ZoneOffset.UTC).toLocalDateTime()
        return "${monthNames[d.monthValue - 1]}, ${fmt.format(d)}"
    }
}