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

data class CurrencyUiState(
    val currency: String = "$"
)

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    var uiState = MutableStateFlow(CurrencyUiState()); private set

    init {
        settingsRepository.currency.load()
            .onEach { currency ->
                uiState.value = uiState.value
                    .copy(currency = currency)
            }.launchIn(viewModelScope)
    }

    fun saveCurrency(currency: String) {
        viewModelScope.launch {
            settingsRepository.currency.save(currency)
        }
    }
}