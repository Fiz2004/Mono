package com.fiz.mono.currency.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    var viewState = MutableStateFlow(CurrencyViewState())
        private set

    init {
        settingsRepository.currency.load()
            .onEach { currency ->
                viewState.value = viewState.value
                    .copy(currency = currency)
            }.launchIn(viewModelScope)
    }

    fun saveCurrency(currency: String) {
        viewModelScope.launch {
            settingsRepository.currency.save(currency)
        }
    }

    fun clickDb() {
        if (viewState.value.currency != "đ") {
            viewState.value = viewState.value
                .copy(currency = "đ")
            viewModelScope.launch {
                settingsRepository.currency.save("đ")
            }
        }
    }
}