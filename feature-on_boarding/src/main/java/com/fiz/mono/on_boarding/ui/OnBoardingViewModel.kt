package com.fiz.mono.on_boarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(private val settingsRepository: SettingsRepository) :
    ViewModel() {
    var uiState = MutableStateFlow(OnBoardingViewState())
        private set

    var viewEffects = MutableSharedFlow<OnBoardingViewEffect>()
        private set

    fun onEvent(event: OnBoardingEvent) {
        when (event) {
            OnBoardingEvent.NextPagesClicked -> nextPagesClicked()
            OnBoardingEvent.SkipButtonClicked -> skipButtonClicked()
            OnBoardingEvent.BackPressClicked -> backPressClicked()
        }
    }

    private fun nextPagesClicked() {
        if (uiState.value.page < 3) {
            val pages = uiState.value.page + 1
            uiState.value = uiState.value
                .copy(page = pages)
        }

        if (uiState.value.page == 3)
            moveNextScreen()

    }

    private fun skipButtonClicked() {
        uiState.value = uiState.value
            .copy(page = 3)

        moveNextScreen()
    }

    private fun backPressClicked() {
        if (uiState.value.page == 0) {
            moveNextScreen()
        } else {
            val pages = uiState.value.page - 1
            uiState.value = uiState.value
                .copy(page = pages)
        }
    }

    private fun moveNextScreen() {
        viewModelScope.launch {
            settingsRepository.firstTime = false
            viewEffects.emit(OnBoardingViewEffect.MoveNextScreen)
        }
    }

    fun getImage() =
        when (uiState.value.page) {
            0 -> com.fiz.mono.feature.on_boarding.R.drawable.illustration1
            1 -> com.fiz.mono.feature.on_boarding.R.drawable.illustration2
            else -> com.fiz.mono.feature.on_boarding.R.drawable.illustration3
        }
}
