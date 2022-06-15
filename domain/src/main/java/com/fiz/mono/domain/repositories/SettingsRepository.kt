package com.fiz.mono.domain.repositories

import com.fiz.mono.domain.models.Property
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

interface SettingsRepository {

    var firstTime: Boolean

    var currency: Property<String>

    var pin: Property<String>

    var needConfirmPin: Property<Boolean>

    var currentConfirmPin: Property<Boolean>

    var theme: Property<Int>

    fun getDate(): LocalDate
    fun setDate(value: LocalDate)

    fun observeDate(): Flow<LocalDate>


}