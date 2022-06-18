package com.fiz.mono.currency.domain

data class Currency(
    val name: String,
    val sign: String,
    val rightSideSign: Boolean = false
)