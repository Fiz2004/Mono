package com.fiz.mono.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.database.dao.CategoryDao
import com.fiz.mono.database.dao.TransactionDao
import com.fiz.mono.database.entity.CategoryEntity
import com.fiz.mono.database.entity.TransactionEntity
import org.threeten.bp.LocalDate

@Database(
    entities = [CategoryEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryItemDao(): CategoryDao
    abstract fun transactionItemDao(): TransactionDao

    companion object {
        fun getAllCategoryExpenseDefault(context: Context) = mutableListOf(
            CategoryEntity("e0", context.getString(R.string.bank), "bank"),
            CategoryEntity("e1", context.getString(R.string.food), "food"),
            CategoryEntity("e2", context.getString(R.string.medican), "medican"),
            CategoryEntity("e3", context.getString(R.string.gym), "gym"),
            CategoryEntity("e4", context.getString(R.string.coffee), "coffee"),
            CategoryEntity("e5", context.getString(R.string.shopping), "market"),
            CategoryEntity("e6", context.getString(R.string.cats), "cat"),
            CategoryEntity("e7", context.getString(R.string.party), "party"),
            CategoryEntity("e8", context.getString(R.string.gift), "gift"),
            CategoryEntity("e9", context.getString(R.string.gas), "gas"),
        )

        fun getAllCategoryIncomeDefault(context: Context) = mutableListOf(
            CategoryEntity("i0", context.getString(R.string.freelance), "challenge"),
            CategoryEntity("i1", context.getString(R.string.salary), "money"),
            CategoryEntity("i2", context.getString(R.string.bonus), "coin"),
            CategoryEntity("i3", context.getString(R.string.loan), "user"),
        )

        fun getTransactionsDefault(context: Context) = mutableListOf(
            TransactionEntity(
                0,
                LocalDate.of(2022, 2, 24),
                -5.49,
                context.getString(R.string.food),
                "Pizza for lazyday",
                "food"
            ),
            TransactionEntity(
                1,
                LocalDate.of(2022, 2, 24),
                50.0,
                context.getString(R.string.freelance),
                "",
                "challenge"
            ),
            TransactionEntity(
                2,
                LocalDate.of(2022, 2, 24),
                -13.16,
                context.getString(R.string.shopping),
                "New Clothes",
                "market"
            ),
            TransactionEntity(
                3,
                LocalDate.of(2022, 2, 24),
                1000.0,
                context.getString(R.string.salary),
                "Jan",
                "money"
            ),
            TransactionEntity(
                4,
                LocalDate.of(2022, 2, 23),
                -3.10,
                context.getString(R.string.food),
                "Pizza",
                "food"
            ),
            TransactionEntity(
                5,
                LocalDate.of(2022, 2, 23),
                50.0,
                context.getString(R.string.loan),
                "",
                "user"
            ),
            TransactionEntity(
                6,
                LocalDate.of(2022, 2, 20),
                -17.50,
                context.getString(R.string.food),
                "Castrang",
                "cat"
            ),
            TransactionEntity(
                7,
                LocalDate.of(2022, 2, 18),
                200.0,
                context.getString(R.string.bonus),
                "Project bonus",
                "coin"
            ),
            TransactionEntity(
                8,
                LocalDate.of(2022, 2, 5),
                10.0,
                context.getString(R.string.bonus),
                "Project bonus",
                "coin"
            )
        )

    }

}


