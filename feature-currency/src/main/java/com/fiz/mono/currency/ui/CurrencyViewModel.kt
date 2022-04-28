package com.fiz.mono.currency.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.repositories.SettingsLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CurrencyUiState(
    val currency: String = "$"
)

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val settingsLocalDataSource: SettingsLocalDataSource
) : ViewModel() {
    var uiState = MutableStateFlow(CurrencyUiState()); private set

    init {
        viewModelScope.launch {
            settingsLocalDataSource.stateFlow.collect {
                uiState.value = uiState.value
                    .copy(currency = it.currency)
            }
        }
    }


    fun setCurrency(currency: String) {
        uiState.value = uiState.value
            .copy(currency = currency)

        viewModelScope.launch {
            settingsLocalDataSource.saveCurrency(currency)
        }
    }

}