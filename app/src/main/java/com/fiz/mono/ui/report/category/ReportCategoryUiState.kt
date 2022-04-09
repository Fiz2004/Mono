package com.fiz.mono.ui.report.category

import android.graphics.*
import com.fiz.mono.ui.models.CategoryUiState
import com.fiz.mono.ui.models.TransactionUiState
import com.fiz.mono.ui.shared_adapters.InfoDay
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoField
import kotlin.math.abs

data class ReportCategoryUiState(
    val allCategoryExpense: List<CategoryUiState> = listOf(),
    val allCategoryIncome: List<CategoryUiState> = listOf(),
    val allTransactions: List<TransactionUiState> = listOf(),
    val reportFor: Int = MONTH,
    val isCanGraph: Boolean = false,
    val isExpense: Boolean = true,
    val id: String = "",
    val list: List<TransactionsDataItem> = listOf(),
    val tabSelectedReport: Int = 0
) {
    val category: CategoryUiState?
        get() = if (isExpense) allCategoryExpense.find { it.id == id } else {
            allCategoryIncome.find { it.id == id }
        }

    private val dateFormatMonthDayYear = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    private val dateFormatMonthMMM = DateTimeFormatter.ofPattern("MMM")
    private val dateFormatDay = DateTimeFormatter.ofPattern("dd")
    private val dateFormatTextMonth = DateTimeFormatter.ofPattern("LLL")
    private val dateFormatTextWeek = DateTimeFormatter.ofPattern("EE")

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
    ): List<TransactionUiState> {
        return allTransactions.filter { it.localDate.year == date.year }
            .filter { it.localDate.month == date.month }
    }

    private fun getAllTransactionsForWeek(
        date: LocalDate
    ): List<TransactionUiState> {
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
        val paintFill = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFill.style = Paint.Style.FILL
        paintFill.shader = getShader(usefulHeight, colorForShader1, colorForShader2)
        paintFill.color = color
        paintFill.strokeWidth = 0f

        val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG)
        paintStroke.style = Paint.Style.STROKE
        paintStroke.color = color
        paintStroke.strokeWidth = 2f * density

        val stepWidth = width / step

        val pathFill = Path()
        val pathStroke = Path()
        pathFill.moveTo(0f, valuesForVertical[0].toFloat())
        pathStroke.moveTo(0f, valuesForVertical[0].toFloat())

        var cX = 0f
        for (n in 1..step.toInt()) {
            cX += if (n == 1)
                stepWidth / 2f
            else
                stepWidth

            val stepHeight = valuesForVertical[n] - valuesForVertical[n - 1]
            pathFill.quadTo(
                cX - stepWidth / 2,
                (valuesForVertical[n] - (stepHeight / 2f)).toFloat(),
                cX,
                valuesForVertical[n].toFloat()
            )
            pathStroke.quadTo(
                cX - stepWidth / 2,
                (valuesForVertical[n] - (stepHeight / 2f)).toFloat(),
                cX,
                valuesForVertical[n].toFloat()
            )
        }
        pathStroke.lineTo(width.toFloat(), valuesForVertical.last().toFloat())
        pathFill.lineTo(width.toFloat(), valuesForVertical.last().toFloat())
        pathFill.lineTo(width.toFloat(), usefulHeight)
        pathFill.lineTo(0f, usefulHeight)
        pathFill.close()
        canvas.drawPath(pathFill, paintFill)
        canvas.drawPath(pathStroke, paintStroke)
    }

    fun drawTextMonth(
        canvas: Canvas, width: Int, height: Int,
        textSize: Float,
        colorText: Int
    ) {
        val paintFont = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFont.textSize = textSize
        paintFont.color = colorText
        paintFont.textAlign = Paint.Align.CENTER

        var currentX = 0f
        var monthDate = LocalDate.now()
        monthDate = monthDate.minusMonths(6)
        val stepWidth = width / 5f

        for (n in 0 until 5) {
            currentX += if (n == 0)
                stepWidth / 2f
            else
                stepWidth

            monthDate = monthDate.plusMonths(1)
            val nameMonth = dateFormatTextMonth.format(monthDate)

            canvas.drawText(nameMonth, currentX, height - paintFont.textSize, paintFont)
        }
    }

    fun drawTextWeek(
        canvas: Canvas, width: Int, height: Int,
        textSize: Float,
        colorText: Int
    ) {
        val paintFont = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFont.textSize = textSize
        paintFont.color = colorText
        paintFont.textAlign = Paint.Align.CENTER

        var currentX = 0f
        var dayDate = LocalDate.now()
        dayDate = dayDate.minusDays(8)
        val stepWidth = width / 7f

        for (n in 0 until 7) {
            currentX += if (n == 0)
                stepWidth / 2f
            else
                stepWidth

            dayDate = dayDate.plusDays(1)
            val nameMonth = dateFormatTextWeek.format(dayDate)

            canvas.drawText(nameMonth, currentX, height - paintFont.textSize, paintFont)
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