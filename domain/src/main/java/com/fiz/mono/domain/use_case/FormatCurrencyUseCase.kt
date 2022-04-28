package com.fiz.mono.domain.use_case

import javax.inject.Inject
import kotlin.math.abs

class FormatCurrencyUseCase @Inject constructor() {

    operator fun invoke(currency: String, value: Double, isPrefixPlus: Boolean): String {
        val prefixPlus = if (isPrefixPlus && value != 0.0) "+" else ""
        return if (value < 0)
            "-" + currency + "%.2f".format(abs(value))
        else
            prefixPlus + currency + "%.2f".format(value)
    }
}