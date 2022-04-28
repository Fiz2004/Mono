package com.fiz.mono.database.repositories

import android.content.SharedPreferences
import com.fiz.mono.domain.repositories.Settings
import com.fiz.mono.domain.repositories.SettingsLocalDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsLocalDataSourceImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    SettingsLocalDataSource {

    override val stateFlow = MutableStateFlow(Settings())

    override suspend fun loadFirstTime() = stateFlow
        .emit(
            stateFlow.value
                .copy(firstTime = sharedPreferences.getBoolean(FIRST_TIME, true))
        )


    override suspend fun saveFirstTime(firstTime: Boolean) {
        sharedPreferences.edit().putBoolean(FIRST_TIME, firstTime).apply()
        stateFlow.value = stateFlow.value
            .copy(firstTime = sharedPreferences.getBoolean(FIRST_TIME, true))
    }

    override fun loadPin(): String = sharedPreferences.getString(PIN, "") ?: ""

    override fun savePin(pin: String) =
        sharedPreferences.edit().putString(PIN, pin).apply()

    override suspend fun loadNeedConfirmPin() = stateFlow
        .emit(
            stateFlow.value
                .copy(isNeedConfirmPIN = sharedPreferences.getBoolean(NEED_CONFIRM_PIN, false))
        )

    override suspend fun saveNeedConfirmPin(confirmPin: Boolean) {
        sharedPreferences.edit().putBoolean(NEED_CONFIRM_PIN, confirmPin).apply()
        stateFlow.value = stateFlow.value
            .copy(isNeedConfirmPIN = sharedPreferences.getBoolean(NEED_CONFIRM_PIN, true))
    }

    override suspend fun loadCurrentConfirmPin() = stateFlow
        .emit(
            stateFlow.value
                .copy(
                    isCurrentConfirmPIN = sharedPreferences.getBoolean(
                        CURRENT_CONFIRM_PIN,
                        false
                    )
                )
        )

    override suspend fun saveCurrentConfirmPin(confirmPin: Boolean) {
        sharedPreferences.edit().putBoolean(NEED_CONFIRM_PIN, confirmPin).apply()
        stateFlow.value = stateFlow.value
            .copy(isCurrentConfirmPIN = sharedPreferences.getBoolean(CURRENT_CONFIRM_PIN, true))
    }

    override suspend fun loadCurrency() = stateFlow
        .emit(
            stateFlow.value
                .copy(currency = sharedPreferences.getString(CURRENCY, "$") ?: "$")
        )

    override suspend fun saveCurrency(currency: String) {
        sharedPreferences.edit().putString(CURRENCY, currency).apply()
        stateFlow.value = stateFlow.value
            .copy(currency = sharedPreferences.getString(CURRENCY, "$") ?: "$")
    }

    override fun loadThemeLight(): Boolean = sharedPreferences.getBoolean(THEME_LIGHT, true)

    override fun saveThemeLight(themeLight: Boolean) =
        sharedPreferences.edit().putBoolean(THEME_LIGHT, themeLight).apply()

    override suspend fun loadDate() = stateFlow
        .emit(
            stateFlow.value
                .copy(
                    date = LocalDate.parse(
                        sharedPreferences.getString(
                            DATE,
                            LocalDate.now().toString()
                        )
                    )
                )
        )

    override suspend fun saveDate(date: LocalDate) {
        sharedPreferences.edit().putString(DATE, date.toString()).apply()
        stateFlow.value = stateFlow.value
            .copy(date = LocalDate.parse(sharedPreferences.getString(DATE, date.toString())))
    }

    companion object {
        const val FIRST_TIME = "FIRST_TIME"
        const val PIN = "PIN"
        const val NEED_CONFIRM_PIN = "NEED_CONFIRM_PIN"
        const val CURRENT_CONFIRM_PIN = "CURRENT_CONFIRM_PIN"
        const val CURRENCY = "CURRENCY"
        const val THEME_LIGHT = "THEME_LIGHT"
        const val DATE = "DATE"
    }
}