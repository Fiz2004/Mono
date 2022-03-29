package com.fiz.mono.ui.report.category

import com.fiz.mono.data.TransactionItem
import com.fiz.mono.ui.report.category.ReportCategoryUtils.getValuesForVerticalForMonth
import com.fiz.mono.ui.report.category.ReportCategoryUtils.getValuesForVerticalForWeek
import com.fiz.mono.util.TimeUtils
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.util.*

class ReportCategoryUtilsTest {

    @Test
    fun getValuesForVerticalForMonth_default() {
        val transactions = listOf(
            TransactionItem(
                0,
                TimeUtils.getDate(2022, 1, 24),
                -5.49,
                "food",
                "Pizza for lazyday",
                "food"
            ),
            TransactionItem(
                1,
                TimeUtils.getDate(2022, 1, 24),
                50.0,
                "challenge",
                "",
                "challenge"
            ),
            TransactionItem(
                2,
                TimeUtils.getDate(2022, 1, 24),
                -13.16,
                "market",
                "New Clothes",
                "market"
            ),
            TransactionItem(
                3,
                TimeUtils.getDate(2022, 1, 24),
                1000.0,
                "money",
                "Jan",
                "money"
            ),
            TransactionItem(
                4,
                TimeUtils.getDate(2022, 1, 23),
                -3.10,
                "food",
                "Pizza",
                "food"
            ),
            TransactionItem(
                5,
                TimeUtils.getDate(2022, 1, 23),
                50.0,
                "user",
                "",
                "user"
            ),
            TransactionItem(
                6,
                TimeUtils.getDate(2022, 1, 20),
                -17.50,
                "cat",
                "Castrang",
                "cat"
            ),
            TransactionItem(
                7,
                TimeUtils.getDate(2022, 1, 18),
                200.0,
                "coin",
                "Project bonus",
                "coin"
            ),
            TransactionItem(
                8,
                TimeUtils.getDate(2022, 0, 5),
                30.0,
                "food",
                "Project bonus",
                "food"
            )
        )
        val currentDate = Calendar.getInstance()
        currentDate.set(2022, 2, 29)
        val monthDate = Calendar.getInstance()
        monthDate.set(2022, 2, 29)
        val result = getValuesForVerticalForMonth(transactions, "food", currentDate, monthDate)

        assertArrayEquals(
            result.toDoubleArray(),
            listOf(0.0, 0.0, 0.0, 0.0, 1.0, 0.2863, 0.0).toDoubleArray(),
            0.001
        )
    }

    @Test
    fun getValuesForVerticalForWeek_default() {
        val transactions = listOf(
            TransactionItem(
                0,
                TimeUtils.getDate(2022, 1, 24),
                -5.49,
                "food",
                "Pizza for lazyday",
                "food"
            ),
            TransactionItem(
                1,
                TimeUtils.getDate(2022, 1, 24),
                50.0,
                "challenge",
                "",
                "challenge"
            ),
            TransactionItem(
                2,
                TimeUtils.getDate(2022, 1, 24),
                -13.16,
                "market",
                "New Clothes",
                "market"
            ),
            TransactionItem(
                3,
                TimeUtils.getDate(2022, 1, 24),
                1000.0,
                "money",
                "Jan",
                "money"
            ),
            TransactionItem(
                4,
                TimeUtils.getDate(2022, 1, 23),
                -3.10,
                "food",
                "Pizza",
                "food"
            ),
            TransactionItem(
                5,
                TimeUtils.getDate(2022, 1, 23),
                50.0,
                "user",
                "",
                "user"
            ),
            TransactionItem(
                6,
                TimeUtils.getDate(2022, 1, 20),
                -17.50,
                "cat",
                "Castrang",
                "cat"
            ),
            TransactionItem(
                7,
                TimeUtils.getDate(2022, 1, 18),
                200.0,
                "coin",
                "Project bonus",
                "coin"
            ),
            TransactionItem(
                8,
                TimeUtils.getDate(2022, 0, 5),
                30.0,
                "food",
                "Project bonus",
                "food"
            )
        )

        val currentDate = Calendar.getInstance()
        currentDate.set(2022, 2, 29)
        val dayDate = Calendar.getInstance()
        dayDate.set(2022, 2, 29)
        val result = getValuesForVerticalForWeek(transactions, "food", currentDate, dayDate)

        assertArrayEquals(
            result.toDoubleArray(),
            listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0).toDoubleArray(),
            0.001
        )
    }
}