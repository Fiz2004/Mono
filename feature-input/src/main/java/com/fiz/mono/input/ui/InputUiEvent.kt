package com.fiz.mono.input.ui

sealed class InputUiEvent {
    data class ChangeTypeTransactions(val typeTransactions: Int?) : InputUiEvent()
    object ClickData : InputUiEvent()
    object ClickBackButton : InputUiEvent()
    object ClickLeftData : InputUiEvent()
    object ClickRightData : InputUiEvent()
    object ClickRemoveTransaction : InputUiEvent()
    object ClickSubmit : InputUiEvent()
    object Start : InputUiEvent()
    object AddPhotoPath : InputUiEvent()
    object MovedOnBoarding : InputUiEvent()
    object MovedPinPassword : InputUiEvent()
    object Returned : InputUiEvent()
    object MovedEdit : InputUiEvent()
    object MovedCalendar : InputUiEvent()
    data class ValueChange(val newValue: String) : InputUiEvent()
    data class NoteChange(val newNote: String) : InputUiEvent()
    data class ClickRemovePhoto(val numberPhoto: Int) : InputUiEvent()
    data class ClickCategory(val position: Int) : InputUiEvent()
    data class Init(val transactionId: Int) : InputUiEvent()
    data class UpdateCurrentPhotoPath(val absolutePath: String) : InputUiEvent()
}