package com.fiz.mono.ui.on_boarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OnBoardingUiState(
    val pages: Int = 0,
    val isNextScreen: Boolean = false
)

class OnBoardingViewModel : ViewModel() {
    private val _onBoardingUiState = MutableStateFlow(OnBoardingUiState())
    val onBoardingUiState: StateFlow<OnBoardingUiState> = _onBoardingUiState.asStateFlow()

    fun clickNextPages() {
        if (onBoardingUiState.value.pages < 3)
            _onBoardingUiState.update {
                it.copy(pages = it.pages + 1)
            }

        if (onBoardingUiState.value.pages == 3) {
            _onBoardingUiState.update {
                it.copy(isNextScreen = true)
            }
        }
    }

    fun clickSkipButton() {
        _onBoardingUiState.update {
            it.copy(
                pages = 3,
                isNextScreen = true
            )
        }
    }

    fun clickBackPress() {
        _onBoardingUiState.update {
            it.copy(pages = it.pages - 1)
        }
    }
}
