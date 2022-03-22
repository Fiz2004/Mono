package com.fiz.mono.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fiz.mono.data.CategoryItem

@Dao
interface CategoryItemDAO {
    @Query("SELECT * FROM CategoryItem")
    fun getAll(): LiveData<List<CategoryItem>>

    @Query("SELECT * FROM CategoryItem WHERE id LIKE '%e%'")
    fun getAllExpense(): LiveData<List<CategoryItem>>

    @Query("SELECT * FROM CategoryItem WHERE id LIKE '%i%'")
    fun getAllIncome(): LiveData<List<CategoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoryItem: CategoryItem)

    @Delete
    suspend fun delete(categoryItem: CategoryItem)
}