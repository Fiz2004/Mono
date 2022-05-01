package com.fiz.mono.domain.models

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class Property<T>(
    getInitValue: () -> T,
    private val _save: (T) -> Unit,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private var property: MutableStateFlow<T> =
        MutableStateFlow(getInitValue())

    fun load(): Flow<T> =
        property

    suspend fun save(property: T) = withContext(defaultDispatcher) {
        _save(property)
        this@Property.property.value = property
    }
}