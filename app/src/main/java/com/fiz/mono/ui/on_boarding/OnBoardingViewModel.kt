package com.fiz.mono.ui.on_boarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OnBoardingUiState(
    val pages: Int = 0,
)

data class OnBoardingNavigationState(
    val isNextScreen: Boolean = false
)

class OnBoardingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OnBoardingUiState())
    val uiState: StateFlow<OnBoardingUiState> = _uiState.asStateFlow()

    private val _navigationUiState = MutableStateFlow(OnBoardingNavigationState())
    val navigationUiState: StateFlow<OnBoardingNavigationState> = _navigationUiState.asStateFlow()

    fun clickNextPages() {
        if (uiState.value.pages < 3)
            _uiState.update {
                it.copy(pages = it.pages + 1)
            }

        if (uiState.value.pages == 3) {
            _navigationUiState.update {
                it.copy(isNextScreen = true)
            }
        }
    }

    fun clickSkipButton() {
        _uiState.update {
            it.copy(
                pages = 3,
            )
        }
        _navigationUiState.update {
            it.copy(
                isNextScreen = true
            )
        }
    }

    fun clickBackPress() {
        _uiState.update {
            it.copy(pages = it.pages - 1)
        }
    }
}
