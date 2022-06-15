package com.fiz.mono.report.ui.category

import android.graphics.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class ReportCategoryGraph(
    private val width: Int,
    private val height: Int,
    color: Int,
    density: Float,
    textSize: Float,
    colorText: Int,
    colorForShader1: Int,
    colorForShader2: Int
) {

    private val paintFont = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.textSize = textSize
        this.color = colorText
        this.textAlign = Paint.Align.CENTER
    }

    private val paintFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.style = Paint.Style.FILL
        this.shader = getShader(height * 0.65f, colorForShader1, colorForShader2)
        this.color = color
        this.strokeWidth = 0f
    }

    private val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.style = Paint.Style.STROKE
        this.color = color
        this.strokeCap = Paint.Cap.ROUND
        this.strokeWidth = 3f * density
    }

    fun drawGraph(canvas: Canvas, state: ReportCategoryViewState) {
        val usefulHeight = height * 0.65f

        if (state.reportFor == PeriodForReport.Month) {
            val valuesByMonth =
                state.getValuesForVerticalForMonth()
                    .map { usefulHeight - usefulHeight * it }
            drawLine(
                canvas,
                width,
                valuesByMonth,
                5f,
                usefulHeight
            )

            val dateFormatTextMonth = DateTimeFormatter.ofPattern("LLL")
            val nameMonth: MutableList<String> = mutableListOf()
            for (n in 5 downTo 1) {
                nameMonth.add(dateFormatTextMonth.format(LocalDate.now().minusMonths(n.toLong())))
            }

            drawText(canvas, width, height, paintFont, nameMonth)
        } else {
            val valuesByWeek =
                state.getValuesForVerticalForWeek()
                    .map { usefulHeight - usefulHeight * it }
            drawLine(
                canvas,
                width,
                valuesByWeek,
                7f,
                usefulHeight
            )

            val dateFormatTextWeek = DateTimeFormatter.ofPattern("EE")
            val nameDays: MutableList<String> = mutableListOf()
            for (n in 7 downTo 1) {
                nameDays.add(dateFormatTextWeek.format(LocalDate.now().minusDays(n.toLong())))
            }

            drawText(canvas, width, height, paintFont, nameDays)
        }
    }

    private fun drawLine(
        canvas: Canvas,
        width: Int,
        valuesForVertical: List<Double>,
        step: Float,
        usefulHeight: Float
    ) {

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

    private fun getShader(usefulHeight: Float, colorForShader1: Int, colorForShader2: Int) =
        LinearGradient(
            0f, 0f, 0f, usefulHeight,
            colorForShader1,
            colorForShader2,
            Shader.TileMode.REPEAT
        )


    private fun drawText(
        canvas: Canvas, width: Int, height: Int,
        paintFont: Paint, namePeriod: List<String>
    ) {
        val spaceForX = width / namePeriod.size.toFloat()

        for (n in namePeriod.indices) {
            val currentX = spaceForX / 2f + spaceForX * n

            canvas.drawText(namePeriod[n], currentX, height - paintFont.textSize, paintFont)
        }
    }
}