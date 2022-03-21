package com.fiz.mono.data.database

import androidx.room.*
import com.fiz.mono.data.CategoryItem

@Dao
interface CategoryItemDAO {
    @Query("SELECT * FROM CategoryItem")
    suspend fun getAll(): List<CategoryItem>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(categoryItem: CategoryItem?)

    @Delete
    fun delete(categoryItem: CategoryItem?)

}