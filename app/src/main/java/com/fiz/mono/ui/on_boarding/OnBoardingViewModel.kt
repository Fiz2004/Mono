package com.fiz.mono.ui.on_boarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnBoardingViewModel : ViewModel() {
    private val _pages = MutableLiveData(0)
    val pages: LiveData<Int> = _pages

    private val _isNextScreen = MutableLiveData(false)
    val isNextScreen: LiveData<Boolean> = _isNextScreen

    fun clickNextPages() {
        if (pages.value!! < 3)
            _pages.value = _pages.value?.plus(1)

        if (pages.value == 3) {
            _isNextScreen.value = true
        }
    }

    fun clickSkipButton() {
        _pages.value = 3
        _isNextScreen.value = true
    }

    fun clickBackPress() {
        _pages.value = _pages.value?.minus(1)
    }

    fun isNextScreenRefresh() {
        _isNextScreen.value = false
    }
}
