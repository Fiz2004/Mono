package com.fiz.mono.core.domain.models

import org.threeten.bp.LocalDate

data class Transaction(
    val id: Int,
    val localDate: LocalDate,
    var value: Double,
    val nameCategory: String,
    val note: String,
    val imgSrc: Int,
    val photo: List<String?> = mutableListOf()
)
