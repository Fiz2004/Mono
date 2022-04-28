package com.fiz.mono.on_boarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.repositories.SettingsLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(private val settingsLocalDataSource: SettingsLocalDataSource) :
    ViewModel() {
    var uiState = MutableStateFlow(OnBoardingUiState()); private set

    var navigationUiState = MutableStateFlow(OnBoardingNavigationState()); private set

    fun onEvent(event: OnBoardingUiEvent) {
        when (event) {
            OnBoardingUiEvent.ClickNextPages -> clickNextPages()
            OnBoardingUiEvent.ClickSkipButton -> clickSkipButton()
            OnBoardingUiEvent.ClickBackPress -> clickBackPress()
        }
    }

    private fun clickNextPages() {
        if (uiState.value.page < 3) {
            val pages = uiState.value.page + 1
            uiState.value = uiState.value
                .copy(page = pages)
        }

        if (uiState.value.page == 3)
            moveNextScreen()

    }

    private fun clickSkipButton() {
        uiState.value = uiState.value
            .copy(page = 3)

        moveNextScreen()
    }

    private fun clickBackPress() {
        if (uiState.value.page == 0) {
            moveNextScreen()
        }else {
            val pages = uiState.value.page - 1
            uiState.value = uiState.value
                .copy(page = pages)
        }
    }

    private fun moveNextScreen() {
        viewModelScope.launch {
            settingsLocalDataSource.saveFirstTime(false)
        }

        navigationUiState.value = navigationUiState.value
            .copy(moveNextScreen = true)
    }

    fun getImage() =
        when (uiState.value.page) {
            0 -> com.fiz.mono.feature.on_boarding.R.drawable.illustration1
            1 -> com.fiz.mono.feature.on_boarding.R.drawable.illustration2
            else -> com.fiz.mono.feature.on_boarding.R.drawable.illustration3
        }
}
