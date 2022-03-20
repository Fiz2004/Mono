package com.fiz.mono.data.database

import androidx.room.*
import com.fiz.mono.data.CategoryItem

@Dao
interface ExpenseCategoryItemDAO {
    @Query("SELECT * FROM CategoryItem")
    fun getAll(): List<CategoryItem>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(categoryItem: CategoryItem?)

    @Delete
    fun delete(categoryItem: CategoryItem?)

}

@Dao
interface IncomeCategoryItemDAO {
    @Query("SELECT * FROM CategoryItem")
    fun getAll(): List<CategoryItem>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(categoryItem: CategoryItem?)

    @Delete
    fun delete(categoryItem: CategoryItem?)

}