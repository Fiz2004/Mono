package com.fiz.mono.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate

@Entity
data class TransactionEntity(
    @PrimaryKey
    val id: Int,
    val localDate: LocalDate,
    var value: Double,
    val nameCategory: String,
    val note: String,
    val mapImgSrc: String,
    val photoPaths: List<String?> = mutableListOf()
)