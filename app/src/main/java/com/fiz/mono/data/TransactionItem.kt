package com.fiz.mono.data

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class TransactionItem(
    @PrimaryKey
    val id: Int,
    val date: Date,
    val value: Double,
    val nameCategory: String,
    val note: String,
    @DrawableRes
    val imgSrc: Int?,
    val photo: List<String?> = mutableListOf()
)