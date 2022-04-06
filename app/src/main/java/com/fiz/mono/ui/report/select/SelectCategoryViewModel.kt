package com.fiz.mono.ui.report.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.CategoryStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SelectUiState(
    val allCategoryExpense: List<CategoryItem> = listOf(),
    val allCategoryIncome: List<CategoryItem> = listOf(),
    val isMoveExpense: Boolean = false,
    val isMoveIncome: Boolean = false,
    val position: Int = -1
)

class SelectCategoryViewModel(private val categoryStore: CategoryStore) : ViewModel() {
    private var _selectUiState = MutableStateFlow(SelectUiState())
    val selectUiState: StateFlow<SelectUiState> = _selectUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _selectUiState.update {
                it.copy(
                    allCategoryExpense = categoryStore.allCategoryExpense.first(),
                    allCategoryIncome = categoryStore.allCategoryIncome.first(),
                )
            }
        }
    }

    fun clickExpenseRecyclerView(position: Int) {
        _selectUiState.update {
            it.copy(
                isMoveExpense = true,
                position = position
            )
        }
    }

    fun clickIncomeRecyclerView(position: Int) {
        _selectUiState.update {
            it.copy(
                isMoveIncome = true,
                position = position
            )
        }
    }
}

class SelectCategoryViewModelFactory(private val categoryStore: CategoryStore) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SelectCategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SelectCategoryViewModel(categoryStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}