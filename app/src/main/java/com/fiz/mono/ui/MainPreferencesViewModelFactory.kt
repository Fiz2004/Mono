package com.fiz.mono.ui

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainPreferencesViewModelFactory(
    private val sharedPreferences: SharedPreferences
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainPreferencesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainPreferencesViewModel(sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}