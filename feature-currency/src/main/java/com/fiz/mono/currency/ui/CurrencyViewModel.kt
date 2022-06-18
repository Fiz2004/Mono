package com.fiz.mono.currency.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    var viewState by mutableStateOf(CurrencyViewState())

    init {
        settingsRepository.currency.load()
            .onEach { currency ->
                viewState = viewState
                    .copy(currency = currency)
            }.launchIn(viewModelScope)
    }

    fun onEvent(event: CurrencyEvent) {
        when (event) {
            is CurrencyEvent.CurrencyItemClicked -> currencyItemClicked(event.currency)
        }
    }

    private fun currencyItemClicked(currency: String) {
        if (viewState.currency != currency) {
            viewModelScope.launch {
                settingsRepository.currency.save(currency)
            }
        }
    }
}

