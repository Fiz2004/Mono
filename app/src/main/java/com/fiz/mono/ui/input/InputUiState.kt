package com.fiz.mono.ui.input

import android.graphics.Bitmap
import com.fiz.mono.R
import com.fiz.mono.ui.models.CategoryUiState
import com.fiz.mono.ui.models.TransactionUiState
import com.fiz.mono.util.BitmapUtils

data class InputUiState(
    val allCategoryExpense: List<CategoryUiState> = listOf(),
    val allCategoryIncome: List<CategoryUiState> = listOf(),
    val allTransactions: List<TransactionUiState> = listOf(),
    val isAllTransactionsLoaded: Boolean = false,
    val note: String = "",
    val value: String = "",
    val selectedAdapter: Int = InputFragment.EXPENSE,
    val transactionId: Int? = null,

    val currentPhotoPath: String = "",
    val photoPaths: List<String?> = listOf(),
    val isPhotoPathsChange: Boolean = false
) {
    val getTextExpenseIncomeTextView: Int
        get() = when (selectedAdapter) {
            InputFragment.EXPENSE -> R.string.expense
            else -> R.string.income
        }

    val transaction: TransactionUiState?
        get() = allTransactions.find { it.id == transactionId }

    val photoBitmap: List<Bitmap?>
        get() = BitmapUtils.getBitmapsFrom(photoPaths = photoPaths)

    val isInput
        get() = transaction == null

    val isEdit
        get() = transaction != null

    val getTextSubmitButton: Int
        get() = if (isInput) R.string.submit else R.string.update

    val currentCategory: List<CategoryUiState>
        get() = if (selectedAdapter == InputFragment.EXPENSE)
            allCategoryExpense
        else
            allCategoryIncome

    fun isClickEditPosition(position: Int): Boolean {
        return if (selectedAdapter == InputFragment.EXPENSE)
            position == allCategoryExpense.size - 1
        else
            position == allCategoryIncome.size - 1
    }


    fun isSubmitButtonEnabled(): Boolean {
        return value.isNotBlank() && currentCategory.firstOrNull { it.selected } != null
    }

}

data class InputNavigationState(
    val isReturn: Boolean = false,
    val isMoveEdit: Boolean = false,
    val isMoveCalendar: Boolean = false,
)