package com.fiz.mono.input.ui

import android.graphics.Bitmap
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.domain.models.Category
import com.fiz.mono.domain.models.Transaction
import com.fiz.mono.util.BitmapUtils

data class InputViewState(
    val allCategoryExpense: List<Category> = listOf(),
    val allCategoryIncome: List<Category> = listOf(),
    val allTransactions: List<Transaction> = listOf(),
    val note: String = "",
    val value: String = "",
    val cashCheckCameraHardware: Boolean? = null,
    val date: String = "",
    val currency: String = "$",
    val selectedAdapter: TypeTransaction = TypeTransaction.Expense,
    val transactionId: Int? = null,
    val currentPhotoPath: String = "",
    val photoPaths: List<String?> = listOf(),
    val isPhotoPathsChange: Boolean = false
) {
    val getTextExpenseIncomeTextView: Int
        get() = when (selectedAdapter) {
            TypeTransaction.Expense -> R.string.expense
            else -> R.string.income
        }

    val transaction: Transaction?
        get() = allTransactions.find { it.id == transactionId }

    val photoBitmap: List<Bitmap?>
        get() = BitmapUtils.getBitmapsFrom(photoPaths = photoPaths)

    val isInput
        get() = transaction == null

    val isEdit
        get() = transaction != null

    val getTextSubmitButton: Int
        get() = if (isInput) R.string.submit else R.string.update

    val currentCategory: List<Category>
        get() = if (selectedAdapter == TypeTransaction.Expense)
            allCategoryExpense
        else
            allCategoryIncome

    fun isClickEditPosition(position: Int): Boolean {
        return if (selectedAdapter == TypeTransaction.Expense) {
            position == allCategoryExpense.size - 1
        } else
            position == allCategoryIncome.size - 1
    }


    fun isSubmitButtonEnabled(): Boolean {
        return value.isNotBlank() && currentCategory.firstOrNull { it.selected } != null
    }

}

