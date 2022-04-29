package com.fiz.mono.database.data_source

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsLocalDataSource @Inject constructor(private val sharedPreferences: SharedPreferences) {

    init {
        sharedPreferences.edit().putBoolean(CURRENT_CONFIRM_PIN, false).apply()
    }

    fun loadFirstTime() = sharedPreferences.getBoolean(FIRST_TIME, true)

    fun saveFirstTime(firstTime: Boolean) {
        sharedPreferences.edit().putBoolean(FIRST_TIME, firstTime).apply()
    }

    fun loadPin(): String = sharedPreferences.getString(PIN, "") ?: ""

    fun savePin(pin: String) =
        sharedPreferences.edit().putString(PIN, pin).apply()

    fun loadNeedConfirmPin() =
        sharedPreferences.getBoolean(NEED_CONFIRM_PIN, false)

    fun saveNeedConfirmPin(confirmPin: Boolean) {
        sharedPreferences.edit().putBoolean(NEED_CONFIRM_PIN, confirmPin).apply()
    }

    fun loadCurrentConfirmPin() =
        sharedPreferences.getBoolean(CURRENT_CONFIRM_PIN, false)

    fun saveCurrentConfirmPin(confirmPin: Boolean) {
        sharedPreferences.edit().putBoolean(CURRENT_CONFIRM_PIN, confirmPin).apply()
    }

    fun loadCurrency() =
        sharedPreferences.getString(CURRENCY, "$") ?: "$"


    fun saveCurrency(currency: String) {
        sharedPreferences.edit().putString(CURRENCY, currency).apply()
    }

    fun loadTheme(): Int =
        sharedPreferences.getInt(THEME, AppCompatDelegate.MODE_NIGHT_UNSPECIFIED)

    fun saveTheme(theme: Int) =
        sharedPreferences.edit().putInt(THEME, theme).apply()

    fun loadDate(): LocalDate =
        LocalDate.parse(
            sharedPreferences.getString(DATE, LocalDate.now().toString())
        )

    fun saveDate(date: LocalDate) {
        sharedPreferences.edit().putString(DATE, date.toString()).apply()
    }

    companion object {
        const val FIRST_TIME = "FIRST_TIME"
        const val PIN = "PIN"
        const val NEED_CONFIRM_PIN = "NEED_CONFIRM_PIN"
        const val CURRENT_CONFIRM_PIN = "CURRENT_CONFIRM_PIN"
        const val CURRENCY = "CURRENCY"
        const val THEME = "THEME_LIGHT"
        const val DATE = "DATE"
    }
}