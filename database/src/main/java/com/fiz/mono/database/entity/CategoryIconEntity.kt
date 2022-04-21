package com.fiz.mono.database.entity

import androidx.annotation.DrawableRes

data class CategoryIconEntity(
    val id: String,
    @DrawableRes
    val imgSrc: Int
)