package com.fiz.mono.domain.repositories

import com.fiz.mono.domain.models.Property
import org.threeten.bp.LocalDate

interface SettingsRepository {

    var currency: Property<String>

    var firstTime: Property<Boolean>

    var pin: Property<String>

    var needConfirmPin: Property<Boolean>

    var currentConfirmPin: Property<Boolean>

    var theme: Property<Int>

    var date: Property<LocalDate>
}