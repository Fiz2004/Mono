package com.fiz.mono.ui.report.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.repositories.CategoryRepository
import com.fiz.mono.ui.models.CategoryUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SelectUiState(
    val allCategoryExpense: List<CategoryUiState> = listOf(),
    val allCategoryIncome: List<CategoryUiState> = listOf(),
    val isMoveExpense: Boolean = false,
    val isMoveIncome: Boolean = false,
    val position: Int = -1
)

class SelectCategoryViewModel(private val categoryRepository: CategoryRepository) : ViewModel() {
    private var _uiState = MutableStateFlow(SelectUiState())
    val uiState: StateFlow<SelectUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    allCategoryExpense = categoryRepository.allCategoryExpense.first(),
                    allCategoryIncome = categoryRepository.allCategoryIncome.first(),
                )
            }
        }
    }

    fun clickExpenseRecyclerView(position: Int) {
        _uiState.update {
            it.copy(
                isMoveExpense = true,
                position = position
            )
        }
    }

    fun clickIncomeRecyclerView(position: Int) {
        _uiState.update {
            it.copy(
                isMoveIncome = true,
                position = position
            )
        }
    }

    fun onMoveExpense() {
        _uiState.update {
            it.copy(
                isMoveExpense = false
            )
        }
    }

    fun onMoveIncome() {
        _uiState.update {
            it.copy(
                isMoveIncome = false
            )
        }
    }
}

class SelectCategoryViewModelFactory(private val categoryRepository: CategoryRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SelectCategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SelectCategoryViewModel(categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}