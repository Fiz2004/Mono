package com.fiz.mono.ui.report.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.repositories.CategoryRepository
import com.fiz.mono.ui.models.CategoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SelectUiState(
    val allCategoryExpense: List<CategoryUiState> = listOf(),
    val allCategoryIncome: List<CategoryUiState> = listOf(),
    val isMoveExpense: Boolean = false,
    val isMoveIncome: Boolean = false,
    val position: Int = -1
)

@HiltViewModel
class SelectCategoryViewModel @Inject constructor(private val categoryRepository: CategoryRepository) :
    ViewModel() {
    private var _uiState = MutableStateFlow(SelectUiState())
    val uiState: StateFlow<SelectUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
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