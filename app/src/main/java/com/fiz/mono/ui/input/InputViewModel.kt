package com.fiz.mono.ui.input

import androidx.lifecycle.ViewModel
import java.util.*

class InputViewModel:ViewModel() {
    // TODO поставить true перед выпуском, сейчас false для отладки
    var firstTime: Boolean = false
    var log: Boolean = false

    var currency: String = "$"

    var PIN: String = ""

    var statePIN = ""

    var date = Calendar.getInstance()

    var tabSelectedReport: Int = 0
}