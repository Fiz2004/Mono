package com.fiz.mono.report.ui.category

import android.graphics.*
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.domain.models.Category
import com.fiz.mono.domain.models.Transaction
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoField
import kotlin.math.abs

data class ReportCategoryUiState(
    val allCategoryExpense: List<Category> = listOf(),
    val allCategoryIncome: List<Category> = listOf(),
    val allTransactions: List<Transaction> = listOf(),
    val reportFor: Int = MONTH,
    val isCanGraph: Boolean = false,
    val isExpense: Boolean = true,
    val id: String = "",
    val list: List<TransactionsDataItem> = listOf(),
    val tabSelectedReport: Int = 0
) {
    val category: Category?
        get() = if (isExpense) allCategoryExpense.find { it.id == id } else {
            allCategoryIncome.find { it.id == id }
        }

    val period
        get() = if (reportFor == MONTH) R.string.this_month else
            R.string.this_week

    private val dateFormatMonthDayYear = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    private val dateFormatMonthMMM = DateTimeFormatter.ofPattern("MMM")
    private val dateFormatDay = DateTimeFormatter.ofPattern("dd")

    fun getTransactions(): List<TransactionsDataItem> {
        var groupTransactions =
            allTransactions.sortedByDescending { it.localDate }.groupBy {
                dateFormatMonthDayYear.format(it.localDate)
            }

        groupTransactions = groupTransactions.mapValues {
            it.value.filter { it.nameCategory == category?.name }
        }.filterValues { it.isNotEmpty() }

        val items = mutableListOf<TransactionsDataItem>()
        for (transactionsForDay in groupTransactions) {
            val expense =
                transactionsForDay.value.filter { it.value < 0 }.map { it.value }
                    .fold(0.0) { acc, d -> acc + d }
            val income =
                transactionsForDay.value.filter { it.value > 0 }.map { it.value }
                    .fold(0.0) { acc, d -> acc + d }
            items += TransactionsDataItem.InfoDayHeaderItem(
                InfoDay(
                    transactionsForDay.key,
                    expense,
                    income
                )
            )
            items += transactionsForDay.value.map { TransactionsDataItem.InfoTransactionItem(it) }
        }
        return items
    }

    fun getValueReportCategory(
        currency: String
    ): String {
        val a = if (reportFor == MONTH)
            getAllTransactionsForMonth(LocalDate.now())
        else
            getAllTransactionsForWeek(LocalDate.now())

        return if (!isExpense) {
            "+"
        } else {
            ""
        } +
                currency +
                abs(a.filter { it.nameCategory == category?.name }
                    .map { it.value }
                    .fold(0.0) { acc, d -> acc + d })
                    .toString()
    }

    private fun getAllTransactionsForMonth(
        date: LocalDate
    ): List<Transaction> {
        return allTransactions.filter { it.localDate.year == date.year }
            .filter { it.localDate.month == date.month }
    }

    private fun getAllTransactionsForWeek(
        date: LocalDate
    ): List<Transaction> {
        return allTransactions.filter { it.localDate.year == date.year }
            .filter { it.localDate.month == date.month }
            .filter { it.localDate.get(ChronoField.ALIGNED_WEEK_OF_MONTH) == date.get(ChronoField.ALIGNED_WEEK_OF_MONTH) }
    }

    fun drawLine(
        canvas: Canvas,
        width: Int,
        color: Int,
        density: Float,
        colorForShader1: Int,
        colorForShader2: Int,
        valuesForVertical: List<Double>,
        step: Float,
        usefulHeight: Float
    ) {
        val paintFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.style = Paint.Style.FILL
            this.shader = getShader(usefulHeight, colorForShader1, colorForShader2)
            this.color = color
            this.strokeWidth = 0f
        }

        val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.style = Paint.Style.STROKE
            this.color = color
            this.strokeCap = Paint.Cap.ROUND
            this.strokeWidth = 3f * density
        }

        val pathStroke = Path()

        val spaceForX = width / step

        pathStroke.moveTo(0f, valuesForVertical[0].toFloat())

        for (n in 1..step.toInt()) {
            val currentX = spaceForX / 2f + spaceForX * (n - 1)

            val stepHeight = valuesForVertical[n] - valuesForVertical[n - 1]
            pathStroke.quadTo(
                currentX - spaceForX / 2,
                (valuesForVertical[n] - (stepHeight / 2f)).toFloat(),
                currentX,
                valuesForVertical[n].toFloat()
            )
        }
        pathStroke.lineTo(width.toFloat(), valuesForVertical.last().toFloat())
        val pathFill = Path(pathStroke).apply {
            lineTo(width.toFloat(), valuesForVertical.last().toFloat())
            lineTo(width.toFloat(), usefulHeight)
            lineTo(0f, usefulHeight)
            close()
        }
        canvas.drawPath(pathFill, paintFill)
        canvas.drawPath(pathStroke, paintStroke)
    }

    fun drawText(
        canvas: Canvas, width: Int, height: Int,
        paintFont: Paint, namePeriod: List<String>
    ) {
        val spaceForX = width / namePeriod.size.toFloat()

        for (n in namePeriod.indices) {
            val currentX = spaceForX / 2f + spaceForX * n

            canvas.drawText(namePeriod[n], currentX, height - paintFont.textSize, paintFont)
        }
    }

    private fun getShader(usefulHeight: Float, colorForShader1: Int, colorForShader2: Int) =
        LinearGradient(
            0f, 0f, 0f, usefulHeight,
            colorForShader1,
            colorForShader2,
            Shader.TileMode.REPEAT
        )

    fun getValuesForVerticalForMonth(): List<Double> {
        val transactionsBy = allTransactions
            .filter {
                it.nameCategory == category?.name &&
                        it.localDate.isAfter(LocalDate.now().minusMonths(7))
            }
            .sortedBy { it.localDate }
            .groupBy {
                dateFormatMonthMMM.format(it.localDate)
            }

        val transactionsByMonth = emptyMap<String, Double>().toMutableMap()
        for (n in 0..6) {
            val nameMonth = dateFormatMonthMMM.format(LocalDate.now().plusMonths(-6 + n.toLong()))
            transactionsByMonth[nameMonth] = transactionsBy
                .filterKeys { it == nameMonth }
                .values
                .firstOrNull()
                ?.fold(0.0) { acc, d -> acc + d.value }
                ?: 0.0
        }

        val max: Double = abs(transactionsByMonth
            .values
            .maxByOrNull { abs(it) } ?: 0.0
        )
        return transactionsByMonth
            .values
            .map { if (max == 0.0) 0.0 else (abs(it) / max) }
            .toList()
    }

    fun getValuesForVerticalForWeek(): List<Double> {
        val transactionsBy = allTransactions
            .filter {
                it.nameCategory == category?.name &&
                        it.localDate.isAfter(LocalDate.now().minusDays(9))
            }
            .sortedBy { it.localDate }
            .groupBy {
                dateFormatDay.format(it.localDate)
            }

        val transactionsByMonth = emptyMap<String, Double>().toMutableMap()
        for (n in 0..8) {
            val nameDay = dateFormatDay.format(LocalDate.now().plusDays(-8 + n.toLong()))
            transactionsByMonth[nameDay] = transactionsBy
                .filterKeys { it == nameDay }
                .values
                .firstOrNull()
                ?.fold(0.0) { acc, d -> acc + d.value }
                ?: 0.0
        }

        val max: Double = abs(transactionsByMonth
            .values
            .maxByOrNull { abs(it) } ?: 0.0
        )
        return transactionsByMonth
            .values
            .map { if (max == 0.0) 0.0 else (abs(it) / max) }
            .toList()
    }

    companion object {
        const val MONTH = 0
        const val WEEK = 1
    }
}