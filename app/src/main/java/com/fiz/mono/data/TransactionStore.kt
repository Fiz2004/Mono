package com.fiz.mono.data

import com.fiz.mono.R
import java.util.*

object TransactionStore {
    private val allTransactions = mutableListOf<TransactionItem>(
        TransactionItem(
            getDate(2022, 1, 24),
            -5.49,
            "Food",
            "Pizza for lazyday",
            R.drawable.food
        ),
        TransactionItem(
            getDate(2022, 1, 24),
            50.0,
            "Freelance",
            "",
            R.drawable.challenge
        ),
        TransactionItem(
            getDate(2022, 1, 24),
            -13.16,
            "Shopping",
            "New Clothes",
            R.drawable.market
        ),
        TransactionItem(
            getDate(2022, 1, 24),
            1000.0,
            "Salary",
            "Jan",
            R.drawable.money
        ),
        TransactionItem(
            getDate(2022, 1, 23),
            -3.10,
            "Food",
            "Pizza",
            R.drawable.food
        ),
        TransactionItem(
            getDate(2022, 1, 23),
            50.0,
            "Loan",
            "",
            R.drawable.user
        ),
        TransactionItem(
            getDate(2022, 1, 20),
            -17.50,
            "Food",
            "Castrang",
            R.drawable.cat
        ),
        TransactionItem(
            getDate(2022, 1, 18),
            200.0,
            "Bonus",
            "Project bonus",
            R.drawable.coin
        )
    )

    fun getAllTransactions() = allTransactions

    fun insertNewTransaction(newTransaction: TransactionItem) {
        allTransactions.add(newTransaction)
    }
}

fun getDate(year: Int, month: Int, day: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    return calendar.time
}