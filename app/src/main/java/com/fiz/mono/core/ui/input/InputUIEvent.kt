package com.fiz.mono.core.ui.input

import org.threeten.bp.LocalDate

sealed class InputUIEvent {
    data class ChangeTypeTransactions(val typeTransactions: Int?) : InputUIEvent()
    class ClickData : InputUIEvent()
    class ClickLeftData : InputUIEvent()
    class ClickRightData : InputUIEvent()
    data class ValueChange(val newValue: String) : InputUIEvent()
    data class NoteChange(val newNote: String) : InputUIEvent()
    class ClickCategory : InputUIEvent()
    data class ClickSubmit(val date: LocalDate) : InputUIEvent()
}