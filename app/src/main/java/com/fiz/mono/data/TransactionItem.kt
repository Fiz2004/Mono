package com.fiz.mono.data

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import java.util.*

data class TransactionItem(
    val date: Date,
    val value: Double,
    val nameCategory: String,
    val note: String,
    @DrawableRes
    val imgSrc: Int?,
    val foto: List<Bitmap>? = null
)