package com.fiz.mono.data.database.dao

import androidx.room.*
import com.fiz.mono.data.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Category")
    fun getAll(): Flow<List<Category>>

    @Query("SELECT * FROM Category WHERE id LIKE '%e%'")
    fun getAllExpense(): Flow<List<Category>>

    @Query("SELECT * FROM Category WHERE id LIKE '%i%'")
    fun getAllIncome(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Category>)

    @Update
    suspend fun update(category: Category)

    @Update
    suspend fun updateAll(categories: List<Category>)

    @Delete
    suspend fun delete(category: Category)
}