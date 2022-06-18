package com.fiz.mono.currency.ui

sealed class CurrencyEvent {
    data class CurrencyItemClicked(val currency: String) : CurrencyEvent()
}