package com.fiz.mono.database.repositories

import com.fiz.mono.database.data_source.SettingsLocalDataSource
import com.fiz.mono.domain.repositories.Property
import com.fiz.mono.domain.repositories.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsLocalDataSource: SettingsLocalDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) :
    SettingsRepository {

    override var currency: Property<String> = Property(
        getInitValue = settingsLocalDataSource::loadCurrency,
        _save = settingsLocalDataSource::saveCurrency,
        defaultDispatcher
    )

    override var firstTime: Property<Boolean> = Property(
        getInitValue = settingsLocalDataSource::loadFirstTime,
        _save = settingsLocalDataSource::saveFirstTime,
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

    override var date: Property<LocalDate> = Property(
        getInitValue = settingsLocalDataSource::loadDate,
        _save = settingsLocalDataSource::saveDate,
        defaultDispatcher
    )
}