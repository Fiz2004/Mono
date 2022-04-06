package com.fiz.mono.data.database.dao

import androidx.room.*
import com.fiz.mono.data.CategoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM CategoryItem")
    fun getAll(): Flow<List<CategoryItem>>

    @Query("SELECT * FROM CategoryItem WHERE id LIKE '%e%'")
    fun getAllExpense(): Flow<List<CategoryItem>>

    @Query("SELECT * FROM CategoryItem WHERE id LIKE '%i%'")
    fun getAllIncome(): Flow<List<CategoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoryItem: CategoryItem)

    @Update
    suspend fun update(categoryItem: CategoryItem)

    @Update
    suspend fun updateAll(categoryItems: List<CategoryItem>)

    @Delete
    suspend fun delete(categoryItem: CategoryItem)
}