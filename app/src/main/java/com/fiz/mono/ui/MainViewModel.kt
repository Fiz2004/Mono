package com.fiz.mono.ui

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionStore
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(
    private val categoryStore: CategoryStore,
    private val transactionStore: TransactionStore,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private var _firstTime = MutableLiveData(false)
    val firstTime: LiveData<Boolean> = _firstTime

    private var _currency = MutableLiveData("$")
    val currency: LiveData<String> = _currency

    private var _pin = MutableLiveData("")
    val pin: LiveData<String> = _pin

    private var _isConfirmPIN = MutableLiveData(false)
    val isConfirmPIN: LiveData<Boolean> = _isConfirmPIN

    private var _date = MutableLiveData(Calendar.getInstance())
    val date: LiveData<Calendar> = _date

    init {
        _firstTime.value = sharedPreferences.getBoolean("firstTime", true)
        setCurrency(sharedPreferences.getString("currency", "$") ?: "$")
        _pin.value = sharedPreferences.getString("PIN", "") ?: ""
        if (pin.value?.isBlank() == true)
            _isConfirmPIN.value = true
    }

    fun getFormatDate(pattern: String): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date.value?.time ?: "")
    }

    fun setMonth(month: Int) {
        _date.value?.set(Calendar.MONTH, month)
        _date.value = date.value
    }

    fun setDate(day: Int) {
        _date.value?.set(Calendar.DATE, day)
        _date.value = date.value
    }

    fun dateDayPlusOne() {
        _date.value?.add(Calendar.DAY_OF_YEAR, 1)
        _date.value = date.value
    }

    fun dateDayMinusOne() {
        _date.value?.add(Calendar.DAY_OF_YEAR, -1)
        _date.value = date.value
    }

    fun dateMonthPlusOne() {
        _date.value?.add(Calendar.MONTH, 1)
        _date.value = date.value
    }

    fun dateMonthMinusOne() {
        _date.value?.add(Calendar.MONTH, -1)
        _date.value = date.value
    }

    fun setCurrency(loadCurrency: String) {
        _currency.value = loadCurrency
    }

    fun changeFirstTime() {
        _firstTime.value = false
        val sharedPreferences = sharedPreferences.edit()
        sharedPreferences.putBoolean("firstTime", firstTime.value ?: false)
        sharedPreferences.apply()
    }

    fun isPin(): Boolean {
        return _pin.value?.isNotBlank() ?: false
    }

    fun deletePin() {
        _pin.value = ""
        sharedPreferences
            .edit()
            .putString("PIN", pin.value)
            .apply()
    }

    fun setPin(pin: String) {
        _pin.value = pin
        sharedPreferences
            .edit()
            .putString("PIN", pin)
            .apply()

    }

    fun confirmPin() {
        _isConfirmPIN.value = true
    }
}