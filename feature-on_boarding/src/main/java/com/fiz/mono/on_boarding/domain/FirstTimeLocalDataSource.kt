package com.fiz.mono.on_boarding.domain

interface FirstTimeLocalDataSource {
    fun loadFirstTime(): Boolean

    fun saveFirstTime(firstTime: Boolean)
}