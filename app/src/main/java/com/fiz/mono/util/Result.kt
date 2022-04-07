package com.fiz.mono.util

sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class Loading<out T>(val status: T) : Result<Nothing>()
}