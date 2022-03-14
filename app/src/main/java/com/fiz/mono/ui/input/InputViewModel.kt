package com.fiz.mono.ui.input

import androidx.lifecycle.ViewModel

class InputViewModel:ViewModel() {
    var firstTime: Boolean = true
    var log: Boolean = false

    var currency: String = "$"
}