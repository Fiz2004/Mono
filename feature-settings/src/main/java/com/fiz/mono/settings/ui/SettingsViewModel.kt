package com.fiz.mono.settings.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fiz.mono.domain.repositories.SettingsLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsLocalDataSource: SettingsLocalDataSource
) : ViewModel() {
    var themeLight = MutableLiveData(settingsLocalDataSource.loadThemeLight()); private set

    fun setThemeLight(themeLight: Boolean) {
        this.themeLight.value = themeLight
        settingsLocalDataSource.saveThemeLight(themeLight)
    }
}