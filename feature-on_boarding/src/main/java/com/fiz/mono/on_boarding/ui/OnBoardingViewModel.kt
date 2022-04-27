package com.fiz.mono.on_boarding.ui

import androidx.lifecycle.ViewModel
import com.fiz.mono.on_boarding.domain.FirstTimeLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(private val firstTimeLocalDataSource: FirstTimeLocalDataSource) :
    ViewModel() {
    var uiState = MutableStateFlow(OnBoardingUiState()); private set

    var navigationUiState = MutableStateFlow(OnBoardingNavigationState()); private set

    fun clickNextPages() {
        if (uiState.value.pages < 3) {
            val pages = uiState.value.pages + 1
            uiState.value = uiState.value
                .copy(pages = pages)
        }

        if (uiState.value.pages == 3) {
            navigationUiState.value = navigationUiState.value
                .copy(isNextScreen = true)
        }
    }

    fun clickSkipButton() {
        uiState.value = uiState.value
            .copy(pages = 3)

        navigationUiState.value = navigationUiState.value
            .copy(isNextScreen = true)
    }

    fun clickBackPress(): Boolean {
        if (uiState.value.pages == 0) return false

        val pages = uiState.value.pages - 1
        uiState.value = uiState.value
            .copy(pages = pages)
        return true
    }

    fun changeFirstTime() {
        firstTimeLocalDataSource.saveFirstTime(false)
    }

    fun getImage() =
        when (uiState.value.pages) {
            0 -> com.fiz.mono.feature.on_boarding.R.drawable.illustration1
            1 -> com.fiz.mono.feature.on_boarding.R.drawable.illustration2
            else -> com.fiz.mono.feature.on_boarding.R.drawable.illustration3
        }
}
