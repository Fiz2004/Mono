package com.fiz.mono.util

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

object TimeUtils {
    fun getNumberLastDayOfWeek(
        date: LocalDate
    ): Int {

        val dateLastDayOfWeek = date.withDayOfMonth(date.lengthOfMonth())
        return dateLastDayOfWeek.dayOfWeek.value
    }

    fun getNumberFirstDayOfWeek(
        date: LocalDate
    ): Int {
        val dateFirstDayOfWeek = date.withDayOfMonth(1)
        return dateFirstDayOfWeek.dayOfWeek.value
    }

    fun getDate(year: Int, month: Int, day: Int): LocalDate {
        return LocalDate.of(year, month, day)
    }

    fun getDateMonthYearString(date: LocalDate, monthNames: Array<String>): String {
        val fmt = DateTimeFormatter.ofPattern("yyyy")
        return "${monthNames[date.monthValue - 1]}, ${fmt.format(date)}"
    }
}