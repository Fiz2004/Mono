package com.fiz.mono.ui.on_boarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnBoardingViewModel : ViewModel() {
    val _pages = MutableLiveData<Int>(0)
    val pages: LiveData<Int>
        get() = _pages

    fun nextPages() {
        if (_pages.value!! < 3)
            _pages.value = _pages.value?.plus(1)
    }

    fun PIN() {
        _pages.value = 2
    }

    fun prevPages() {
        _pages.value = _pages.value?.minus(1)
    }
}
