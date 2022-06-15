package com.fiz.mono.input.ui

sealed class InputEvent {
    object DataTextClicked : InputEvent()
    object BackButtonClicked : InputEvent()
    object LeftDataIconClicked : InputEvent()
    object RightDataIconClicked : InputEvent()
    object RemoveTransactionButtonClicked : InputEvent()
    object SubmitButtonClicked : InputEvent()
    object AddPhotoPathButtonClicked : InputEvent()
    data class TypeTransactionChanged(val typeTransactions: TypeTransaction) : InputEvent()
    data class ValueTransactionChanged(val value: String) : InputEvent()
    data class NoteTransactionChanged(val value: String) : InputEvent()
    data class RemovePhotoButtonClicked(val numberPhoto: Int) : InputEvent()
    data class CategoryItemCardClicked(val position: Int) : InputEvent()
    data class Init(val transactionId: Int) : InputEvent()
    data class UpdateCurrentPhotoPath(val absolutePath: String) : InputEvent()
}

enum class TypeTransaction {
    Income, Expense
}