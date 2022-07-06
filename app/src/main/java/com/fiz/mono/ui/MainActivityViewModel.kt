package com.fiz.mono.ui

import android.content.res.Configuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    var theme: Int = Configuration.UI_MODE_NIGHT_NO; private set

    init {
        settingsRepository.theme.load()
            .onEach { theme ->
                this.theme = theme
            }.launchIn(viewModelScope)
    }

    suspend fun loadTheme(): Int {
        return settingsRepository.theme.load().first()
    }

    suspend fun checkThemeFirstTime(currentTheme: Int) {
        if (settingsRepository.firstTime)
            settingsRepository.theme.save(currentTheme)
    }

}