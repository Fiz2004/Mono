package com.fiz.mono.ui.report.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.repositories.CategoryRepository
import com.fiz.mono.ui.models.CategoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
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
    var uiState = MutableStateFlow(SelectUiState())
        private set

    init {
        viewModelScope.launch(Dispatchers.Default) {
            categoryRepository.getAllCategoryExpenseForInput().collect { allCategoryExpense ->
                uiState.update {
                    it.copy(
                        allCategoryExpense = allCategoryExpense
                    )
                }
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            categoryRepository.getAllCategoryIncomeForInput().collect { allCategoryIncome ->
                uiState.update {
                    it.copy(
                        allCategoryIncome = allCategoryIncome
                    )
                }
            }
        }
    }

    fun clickExpenseRecyclerView(position: Int) {
        uiState.update {
            it.copy(
                isMoveExpense = true,
                position = position
            )
        }
    }

    fun clickIncomeRecyclerView(position: Int) {
        uiState.update {
            it.copy(
                isMoveIncome = true,
                position = position
            )
        }
    }

    fun onMoveExpense() {
        uiState.update {
            it.copy(
                isMoveExpense = false
            )
        }
    }

    fun onMoveIncome() {
        uiState.update {
            it.copy(
                isMoveIncome = false
            )
        }
    }
}