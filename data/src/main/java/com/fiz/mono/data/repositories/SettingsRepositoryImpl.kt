package com.fiz.mono.data.repositories

import com.fiz.mono.data.data_source.SettingsLocalDataSource
import com.fiz.mono.domain.models.Property
import com.fiz.mono.domain.repositories.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsLocalDataSource: SettingsLocalDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SettingsRepository {

    override var firstTime: Boolean
        get() = settingsLocalDataSource.loadFirstTime()
        set(value) {
            settingsLocalDataSource.saveFirstTime(value)
        }

    override var currency: Property<String> = Property(
        getInitValue = settingsLocalDataSource::loadCurrency,
        _save = settingsLocalDataSource::saveCurrency,
        defaultDispatcher
    )

    override var pin: Property<String> = Property(
        getInitValue = settingsLocalDataSource::loadPin,
        _save = settingsLocalDataSource::savePin,
        defaultDispatcher
    )

    override var needConfirmPin: Property<Boolean> = Property(
        getInitValue = settingsLocalDataSource::loadNeedConfirmPin,
        _save = settingsLocalDataSource::saveNeedConfirmPin,
        defaultDispatcher
    )

    override var currentConfirmPin: Property<Boolean> = Property(
        getInitValue = settingsLocalDataSource::loadCurrentConfirmPin,
        _save = settingsLocalDataSource::saveCurrentConfirmPin,
        defaultDispatcher
    )

    override var theme: Property<Int> = Property(
        getInitValue = settingsLocalDataSource::loadTheme,
        _save = settingsLocalDataSource::saveTheme,
        defaultDispatcher
    )

    private var date: MutableStateFlow<LocalDate> =
        MutableStateFlow(settingsLocalDataSource.loadDate())

    override fun getDate(): LocalDate {
        return settingsLocalDataSource.loadDate()
    }

    override fun observeDate(): Flow<LocalDate> {
        return date
    }

    override fun setDate(value: LocalDate) {
        settingsLocalDataSource.saveDate(value)
        CoroutineScope(defaultDispatcher).launch {
            date.emit(value)
        }
    }

}