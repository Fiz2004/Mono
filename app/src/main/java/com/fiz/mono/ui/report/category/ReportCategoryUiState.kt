package com.fiz.mono.ui.report.category

import android.graphics.*
import com.fiz.mono.ui.models.CategoryUiState
import com.fiz.mono.ui.models.TransactionUiState
import com.fiz.mono.ui.shared_adapters.InfoDay
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import java.text.SimpleDateFormat
import java.util.*
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

    fun getTransactions(): List<TransactionsDataItem> {
        var groupTransactions =
            allTransactions.sortedByDescending { it.date.time }.groupBy {
                SimpleDateFormat(
                    "MMM dd, yyyy",
                    Locale.getDefault()
                ).format(it.date.time)
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
            getAllTransactionsForMonth(Calendar.getInstance())
        else
            getAllTransactionsForWeek(Calendar.getInstance())

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
        date: Calendar
    ): List<TransactionUiState> {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)

        val allTransactionsForYear =
            allTransactions.filter {
                SimpleDateFormat(
                    "yyyy",
                    Locale.getDefault()
                ).format(it.date.time) == currentYear.toString()
            }
        val allTransactionsForMonth =
            allTransactionsForYear.filter {
                SimpleDateFormat(
                    "M",
                    Locale.getDefault()
                ).format(it.date.time) == (currentMonth + 1).toString()
            }
        return allTransactionsForMonth
    }

    private fun getAllTransactionsForWeek(
        date: Calendar
    ): List<TransactionUiState> {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)
        val currentWeek = date.get(Calendar.WEEK_OF_YEAR)

        val allTransactionsForYear =
            allTransactions.filter {
                SimpleDateFormat(
                    "yyyy",
                    Locale.getDefault()
                ).format(it.date.time) == currentYear.toString()
            }
        val allTransactionsForMonth =
            allTransactionsForYear.filter {
                SimpleDateFormat(
                    "M",
                    Locale.getDefault()
                ).format(it.date.time) == (currentMonth + 1).toString()
            }
        val allTransactionsForWeek =
            allTransactionsForMonth.filter {
                SimpleDateFormat(
                    "w",
                    Locale.getDefault()
                ).format(it.date.time) == (currentWeek).toString()
            }
        return allTransactionsForWeek
    }

    fun drawLineMonth(
        canvas: Canvas,
        width: Int,
        height: Int,
        color: Int,
        density: Float,
        colorForShader1: Int,
        colorForShader2: Int
    ) {
        val usefulHeight = height * 0.65f

        val paintFill = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFill.style = Paint.Style.FILL
        paintFill.shader = getShader(usefulHeight, colorForShader1, colorForShader2)
        paintFill.color = color
        paintFill.strokeWidth = 0f

        val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG)
        paintStroke.style = Paint.Style.STROKE
        paintStroke.color = color
        paintStroke.strokeWidth = 2f * density

        val valuesByMonth =
            ReportCategoryUtils.getValuesForVerticalForMonth(
                allTransactions, category?.name,
                Calendar.getInstance(), Calendar.getInstance()
            ).map {
                usefulHeight - usefulHeight * it
            }

        val stepWidth = width / 5f

        val pathFill = Path()
        val pathStroke = Path()
        pathFill.moveTo(0f, valuesByMonth[0].toFloat())
        pathStroke.moveTo(0f, valuesByMonth[0].toFloat())

        var cX = 0f
        for (n in 1..5) {
            cX += if (n == 1)
                stepWidth / 2f
            else
                stepWidth

            val stepHeight = valuesByMonth[n] - valuesByMonth[n - 1]
            pathFill.quadTo(
                cX - stepWidth / 2,
                (valuesByMonth[n] - (stepHeight / 2f)).toFloat(),
                cX,
                valuesByMonth[n].toFloat()
            )
            pathStroke.quadTo(
                cX - stepWidth / 2,
                (valuesByMonth[n] - (stepHeight / 2f)).toFloat(),
                cX,
                valuesByMonth[n].toFloat()
            )
        }
        pathStroke.lineTo(width.toFloat(), valuesByMonth[valuesByMonth.size - 1].toFloat())
        pathFill.lineTo(width.toFloat(), valuesByMonth[valuesByMonth.size - 1].toFloat())
        pathFill.lineTo(width.toFloat(), usefulHeight.toFloat())
        pathFill.lineTo(0f, usefulHeight.toFloat())
        pathFill.close()
        canvas.drawPath(pathFill, paintFill)
        canvas.drawPath(pathStroke, paintStroke)
    }

    fun drawLineWeek(
        canvas: Canvas,
        width: Int,
        height: Int,
        color: Int,
        density: Float,
        colorForShader1: Int,
        colorForShader2: Int
    ) {
        val usefulHeight = height * 0.65f

        val paintFill = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFill.style = Paint.Style.FILL
        paintFill.shader = getShader(usefulHeight, colorForShader1, colorForShader2)
        paintFill.color = color
        paintFill.strokeWidth = 0f

        val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG)
        paintStroke.style = Paint.Style.STROKE
        paintStroke.color = color
        paintStroke.strokeWidth = 2f * density

        val valuesByWeek =
            ReportCategoryUtils.getValuesForVerticalForWeek(
                allTransactions, category?.name,
                Calendar.getInstance(), Calendar.getInstance()
            ).map {
                usefulHeight - usefulHeight * it
            }

        val stepWidth = width / 7f

        val pathFill = Path()
        val pathStroke = Path()
        pathFill.moveTo(0f, valuesByWeek[0].toFloat())
        pathStroke.moveTo(0f, valuesByWeek[0].toFloat())

        var cX = 0f
        for (n in 1..7) {
            cX += if (n == 1)
                stepWidth / 2f
            else
                stepWidth

            val stepHeight = valuesByWeek[n] - valuesByWeek[n - 1]

            pathFill.quadTo(
                cX - stepWidth / 2,
                (valuesByWeek[n] - (stepHeight / 2f)).toFloat(),
                cX,
                valuesByWeek[n].toFloat()
            )
            pathStroke.quadTo(
                cX - stepWidth / 2,
                (valuesByWeek[n] - (stepHeight / 2f)).toFloat(),
                cX,
                valuesByWeek[n].toFloat()
            )
        }
        pathStroke.lineTo(width.toFloat(), valuesByWeek[valuesByWeek.size - 1].toFloat())
        pathFill.lineTo(width.toFloat(), valuesByWeek[valuesByWeek.size - 1].toFloat())
        pathFill.lineTo(width.toFloat(), usefulHeight.toFloat())
        pathFill.lineTo(0f, usefulHeight.toFloat())
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
        val monthDate = Calendar.getInstance()
        monthDate.add(Calendar.MONTH, -6)
        val stepWidth = width / 5f

        for (n in 0 until 5) {
            currentX += if (n == 0)
                stepWidth / 2f
            else
                stepWidth

            monthDate.add(Calendar.MONTH, 1)
            val nameMonth = SimpleDateFormat(
                "LLL",
                Locale.getDefault()
            ).format(monthDate.time)

            canvas.drawText("$nameMonth", currentX, height - paintFont.textSize, paintFont)
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
        val dayDate = Calendar.getInstance()
        dayDate.add(Calendar.DATE, -8)
        val stepWidth = width / 7f

        for (n in 0 until 7) {
            currentX += if (n == 0)
                stepWidth / 2f
            else
                stepWidth

            dayDate.add(Calendar.DATE, 1)
            val nameMonth = SimpleDateFormat(
                "EE",
                Locale.getDefault()
            ).format(dayDate.time)

            canvas.drawText("$nameMonth", currentX, height - paintFont.textSize, paintFont)
        }
    }


    private fun getShader(usefulHeight: Float, colorForShader1: Int, colorForShader2: Int) =
        LinearGradient(
            0f, 0f, 0f, usefulHeight,
            colorForShader1,
            colorForShader2,
            Shader.TileMode.REPEAT
        )


    companion object {
        const val MONTH = 0
        const val WEEK = 1
    }
}