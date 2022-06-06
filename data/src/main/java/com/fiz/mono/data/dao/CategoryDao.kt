package com.fiz.mono.data.dao

import androidx.room.*
import com.fiz.mono.data.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM CategoryEntity")
    fun getAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM CategoryEntity WHERE id LIKE '%e%'")
    fun getAllExpense(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM CategoryEntity WHERE id LIKE '%i%'")
    fun getAllIncome(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoryEntity: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categoryEntities: List<CategoryEntity>)

    @Update
    suspend fun update(categoryEntity: CategoryEntity)

    @Update
    suspend fun updateAll(categoryEntities: List<CategoryEntity>)

    @Delete
    suspend fun delete(categoryEntity: CategoryEntity)

    @Query("DELETE FROM CategoryEntity")
    suspend fun deleteAll()
}