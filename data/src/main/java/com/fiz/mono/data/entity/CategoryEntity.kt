package com.fiz.mono.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val mapImgSrc: String
)