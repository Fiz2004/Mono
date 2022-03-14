package com.fiz.mono.data

import androidx.annotation.DrawableRes

data class CategoryItem(
    val name: String,
    @DrawableRes val imgSrc: Int?
)