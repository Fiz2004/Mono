package com.fiz.mono.ui.input

import android.graphics.Bitmap
import com.fiz.mono.ui.models.CategoryUiState
import com.fiz.mono.ui.models.TransactionUiState
import com.fiz.mono.util.BitmapUtils

data class InputUiState(
    val isReturn: Boolean = false,
    val isMoveEdit: Boolean = false,
    val isMoveCalendar: Boolean = false,
    val allCategoryExpense: List<CategoryUiState> = listOf(),
    val allCategoryIncome: List<CategoryUiState> = listOf(),
    val allTransactions: List<TransactionUiState> = listOf(),
    val note: String = "",
    val value: String = "",
    val selectedAdapter: Int = InputFragment.EXPENSE,
    val transaction: TransactionUiState? = null,
    val photoPaths: MutableList<String?> = mutableListOf(),
    val isPhotoPathsChange: Boolean = false
) {
    val photoBitmap: List<Bitmap?>
        get() = BitmapUtils.getBitmapsFrom(photoPaths = photoPaths)
}