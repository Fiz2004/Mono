package com.fiz.mono.currency.data

import com.fiz.mono.currency.domain.Currency

val currencies = listOf(
    Currency("$", "USD"),
    Currency("₽", "RUB"),
    Currency("¥", "JPY"),
    Currency("₡", "CRC"),
    Currency("£", "GBP"),
    Currency("₼", "AZN"),
    Currency("€", "ALL"),
    Currency("лв", "BGN"),
    Currency("đ", "VND", true)
)