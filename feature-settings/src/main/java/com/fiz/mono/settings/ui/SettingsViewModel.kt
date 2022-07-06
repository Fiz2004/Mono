package com.fiz.mono.settings.ui

import android.content.res.Configuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.data_source.CategoryLocalDataSource
import com.fiz.mono.data.data_source.TransactionLocalDataSource
import com.fiz.mono.domain.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val transactionLocalDataSource: TransactionLocalDataSource,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    var theme: Int = Configuration.UI_MODE_NIGHT_NO
        private set

    init {
        settingsRepository.theme.load()
            .onEach { theme ->
                this.theme = theme
            }.launchIn(viewModelScope)
    }

    fun clickSwitchTheme(theme: Int) {
        viewModelScope.launch {
            settingsRepository.theme.save(theme)
        }
    }

    fun clickDelete() {
        viewModelScope.launch {
            categoryLocalDataSource.deleteAll()
            transactionLocalDataSource.deleteAll()
        }
    }
}