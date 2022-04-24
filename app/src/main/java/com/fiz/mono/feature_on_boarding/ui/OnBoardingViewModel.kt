package com.fiz.mono.core.ui.on_boarding

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class OnBoardingUiState(
    val pages: Int = 0,
)

data class OnBoardingNavigationState(
    val isNextScreen: Boolean = false
)


@HiltViewModel
class OnBoardingViewModel @Inject constructor() : ViewModel() {
    var uiState = MutableStateFlow(OnBoardingUiState())
        private set

    var navigationUiState = MutableStateFlow(OnBoardingNavigationState())
        private set

    fun clickNextPages() {
        if (uiState.value.pages < 3)
            uiState.update {
                it.copy(pages = it.pages + 1)
            }

        if (uiState.value.pages == 3) {
            navigationUiState.update {
                it.copy(isNextScreen = true)
            }
        }
    }

    fun clickSkipButton() {
        uiState.update {
            it.copy(
                pages = 3,
            )
        }
        navigationUiState.update {
            it.copy(
                isNextScreen = true
            )
        }
    }

    fun clickBackPress() {
        uiState.update {
            it.copy(pages = it.pages - 1)
        }
    }
}
