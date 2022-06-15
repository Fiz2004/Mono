package com.fiz.mono.data.entity

import androidx.annotation.DrawableRes

data class IconCategoryEntity(
    val id: String,
    @DrawableRes
    val imgSrc: Int
)