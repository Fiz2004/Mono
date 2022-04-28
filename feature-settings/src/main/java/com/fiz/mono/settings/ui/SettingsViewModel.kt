package com.fiz.mono.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.database.data_source.CategoryLocalDataSource
import com.fiz.mono.database.data_source.TransactionLocalDataSource
import com.fiz.mono.domain.repositories.SettingsLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val transactionLocalDataSource: TransactionLocalDataSource,
    private val settingsLocalDataSource: SettingsLocalDataSource
) : ViewModel() {
    var themeLight = settingsLocalDataSource.loadThemeLight(); private set

    fun setThemeLight(themeLight: Boolean) {
        this.themeLight = themeLight
        settingsLocalDataSource.saveThemeLight(themeLight)
    }

    fun clickDelete() {
        viewModelScope.launch {
            categoryLocalDataSource.deleteAll()
            transactionLocalDataSource.deleteAll()
        }
    }
}