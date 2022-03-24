package com.fiz.mono.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class TransactionItem(
    @PrimaryKey
    val id: Int,
    val date: Date,
    var value: Double,
    val nameCategory: String,
    val note: String,
    val mapImgSrc: String,
    val photo: List<String?> = mutableListOf()
)