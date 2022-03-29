package com.fiz.mono.ui.report.category

import com.fiz.mono.data.TransactionItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

object ReportCategoryUtils {
    fun getValuesForVerticalForMonth(
        transactions: List<TransactionItem>? = emptyList(), categoryName: String?,
        currentDate: Calendar, monthDate: Calendar
    ): List<Double> {
        monthDate.add(Calendar.MONTH, -7)
        val countMSInDay = 1000 * 60 * 60 * 24
        val dayBetween = (currentDate.time.time - monthDate.time.time) / countMSInDay

        val transactionsBy = transactions
            ?.filter { it.nameCategory == categoryName }
            ?.filter { it.date.time > (currentDate.time.time - dayBetween * countMSInDay) }
            ?.sortedBy { it.date }
            ?.groupBy {
                SimpleDateFormat(
                    "MMM",
                    Locale.getDefault()
                ).format(it.date.time)
            }

        val transactionsByMonth = emptyMap<String, Double>().toMutableMap()
        for (n in 0..6) {
            monthDate.add(Calendar.MONTH, 1)
            val nameMonth = SimpleDateFormat(
                "MMM",
                Locale.getDefault()
            ).format(monthDate.time)
            transactionsByMonth[nameMonth] = transactionsBy
                ?.filterKeys { it == nameMonth }
                ?.values
                ?.firstOrNull()
                ?.fold(0.0) { acc, d -> acc + d.value }
                ?: 0.0
        }

        val max: Double = abs(transactionsByMonth
            .values
            .maxByOrNull { abs(it) } ?: 0.0
        )
        val result = transactionsByMonth
            .values
            .map { if (max == 0.0) 0.0 else (abs(it) / max) }
            .toList()
        return result
    }

    fun getValuesForVerticalForWeek(
        transactions: List<TransactionItem>? = emptyList(), categoryName: String?,
        currentDate: Calendar, DayDate: Calendar
    ): List<Double> {
        DayDate.add(Calendar.DATE, -9)
        val countMSInDay = 1000 * 60 * 60 * 24
        val dayBetween = (currentDate.time.time - DayDate.time.time) / countMSInDay

        val transactionsBy = transactions
            ?.filter { it.nameCategory == categoryName }
            ?.filter { it.date.time > (currentDate.time.time - dayBetween * countMSInDay) }
            ?.sortedBy { it.date }
            ?.groupBy {
                SimpleDateFormat(
                    "dd",
                    Locale.getDefault()
                ).format(it.date.time)
            }

        val transactionsByMonth = emptyMap<String, Double>().toMutableMap()
        for (n in 0..8) {
            DayDate.add(Calendar.DATE, 1)
            val nameDay = SimpleDateFormat(
                "dd",
                Locale.getDefault()
            ).format(DayDate.time)
            transactionsByMonth[nameDay] = transactionsBy
                ?.filterKeys { it == nameDay }
                ?.values
                ?.firstOrNull()
                ?.fold(0.0) { acc, d -> acc + d.value }
                ?: 0.0
        }

        val max: Double = abs(transactionsByMonth
            .values
            .maxByOrNull { abs(it) } ?: 0.0
        )
        val valuesByMonth = transactionsByMonth
            .values
            .map { if (max == 0.0) 0.0 else (abs(it) / max) }
            .toList()
        return valuesByMonth
    }
}