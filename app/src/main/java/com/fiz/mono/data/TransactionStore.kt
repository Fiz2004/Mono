package com.fiz.mono.data

import com.fiz.mono.data.database.ItemDatabase
import com.fiz.mono.data.database.TransactionItemDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.Executors

object TransactionStore {
    private lateinit var allTransactions: MutableList<TransactionItem>

    private val transactionItemDao: TransactionItemDAO? =
        ItemDatabase.getDatabase()?.transactionItemDao()

    suspend fun init(callback: () -> Unit) {
        var loadItemDao: List<TransactionItem>? = null

        try {
            loadItemDao = transactionItemDao?.getAll()
            if (loadItemDao?.size == 0 || loadItemDao == null) {
                allTransactions = mutableListOf(
                    TransactionItem(
                        0,
                        getDate(2022, 1, 24),
                        -5.49,
                        "Food",
                        "Pizza for lazyday",
                        "food"
                    ),
                    TransactionItem(
                        1,
                        getDate(2022, 1, 24),
                        50.0,
                        "Freelance",
                        "",
                        "challenge"
                    ),
                    TransactionItem(
                        2,
                        getDate(2022, 1, 24),
                        -13.16,
                        "Shopping",
                        "New Clothes",
                        "market"
                    ),
                    TransactionItem(
                        3,
                        getDate(2022, 1, 24),
                        1000.0,
                        "Salary",
                        "Jan",
                        "money"
                    ),
                    TransactionItem(
                        4,
                        getDate(2022, 1, 23),
                        -3.10,
                        "Food",
                        "Pizza",
                        "food"
                    ),
                    TransactionItem(
                        5,
                        getDate(2022, 1, 23),
                        50.0,
                        "Loan",
                        "",
                        "user"
                    ),
                    TransactionItem(
                        6,
                        getDate(2022, 1, 20),
                        -17.50,
                        "Food",
                        "Castrang",
                        "cat"
                    ),
                    TransactionItem(
                        7,
                        getDate(2022, 1, 18),
                        200.0,
                        "Bonus",
                        "Project bonus",
                        "coin"
                    )
                )
                withContext(Dispatchers.Default) {
                    allTransactions.forEach {
                        transactionItemDao?.insert(it)
                    }
                }
            } else {
                allTransactions =
                    loadItemDao as MutableList<TransactionItem>
            }
            callback()
        } catch (e: Exception) {
            allTransactions = mutableListOf(
                TransactionItem(
                    0,
                    getDate(2022, 1, 24),
                    -5.49,
                    "Food",
                    "Pizza for lazyday",
                    "food"
                ),
                TransactionItem(
                    1,
                    getDate(2022, 1, 24),
                    50.0,
                    "Freelance",
                    "",
                    "challenge"
                ),
                TransactionItem(
                    2,
                    getDate(2022, 1, 24),
                    -13.16,
                    "Shopping",
                    "New Clothes",
                    "market"
                ),
                TransactionItem(
                    3,
                    getDate(2022, 1, 24),
                    1000.0,
                    "Salary",
                    "Jan",
                    "money"
                ),
                TransactionItem(
                    4,
                    getDate(2022, 1, 23),
                    -3.10,
                    "Food",
                    "Pizza",
                    "food"
                ),
                TransactionItem(
                    5,
                    getDate(2022, 1, 23),
                    50.0,
                    "Loan",
                    "",
                    "user"
                ),
                TransactionItem(
                    6,
                    getDate(2022, 1, 20),
                    -17.50,
                    "Food",
                    "Castrang",
                    "cat"
                ),
                TransactionItem(
                    7,
                    getDate(2022, 1, 18),
                    200.0,
                    "Bonus",
                    "Project bonus",
                    "coin"
                )
            )
            withContext(Dispatchers.Default) {
                allTransactions.forEach {
                    transactionItemDao?.insert(it)
                }
            }
            callback()
        }
    }

    fun getAllTransactions(): List<TransactionItem> {
        val result = allTransactions.map { it.copy() }
        return result
    }


    fun insertNewTransaction(newTransaction: TransactionItem) {
        Executors.newSingleThreadExecutor().execute {
            allTransactions.add(newTransaction)
            transactionItemDao?.insert(newTransaction)
        }
    }
}

fun getDate(year: Int, month: Int, day: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    return calendar.time
}