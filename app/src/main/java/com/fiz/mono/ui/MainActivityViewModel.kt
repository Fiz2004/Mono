package com.fiz.mono.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import com.fiz.mono.domain.repositories.SettingsLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    settingsLocalDataSource: SettingsLocalDataSource
) : ViewModel() {

    var themeLight = settingsLocalDataSource.loadThemeLight(); private set


    fun getMode():Int{
        return if (themeLight)
            AppCompatDelegate.MODE_NIGHT_NO
        else
            AppCompatDelegate.MODE_NIGHT_YES
    }
}