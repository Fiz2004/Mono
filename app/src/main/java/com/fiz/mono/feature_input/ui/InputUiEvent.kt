package com.fiz.mono.feature_input.ui

import org.threeten.bp.LocalDate

sealed class InputUiEvent {
    data class ChangeTypeTransactions(val typeTransactions: Int?) : InputUiEvent()
    object ClickData : InputUiEvent()
    object ClickLeftData : InputUiEvent()
    object ClickRightData : InputUiEvent()
    data class ValueChange(val newValue: String) : InputUiEvent()
    data class NoteChange(val newNote: String) : InputUiEvent()
    object ClickCategory : InputUiEvent()
    data class ClickSubmit(val date: LocalDate) : InputUiEvent()
}