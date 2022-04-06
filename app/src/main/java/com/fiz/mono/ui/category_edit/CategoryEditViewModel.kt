package com.fiz.mono.ui.category_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.fiz.mono.data.data_source.CategoryDataSource
import com.fiz.mono.ui.models.CategoryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryEditUiState(
    val allCategoryExpense: List<CategoryUiState> = listOf(),
    val allCategoryIncome: List<CategoryUiState> = listOf(),
)

class CategoryEditViewModel(
    private val categoryDataSource: CategoryDataSource
) : ViewModel() {
    private val _categoryEditUiState = MutableStateFlow(CategoryEditUiState())
    val categoryEditUiState: StateFlow<CategoryEditUiState> = _categoryEditUiState.asStateFlow()

    private val _isReturn = MutableLiveData(false)
    val isReturn: LiveData<Boolean> = _isReturn

    private val _isMoveAdd = MutableLiveData(false)
    val isMoveAdd: LiveData<Boolean> = _isMoveAdd

    private val _isSelected = MutableLiveData(false)
    val isSelected: LiveData<Boolean> = _isSelected

    var type: String = TYPE_EXPENSE

    init {
        viewModelScope.launch {
            categoryDataSource.getAllCategoryExpenseForEdit().collect { list ->
                _categoryEditUiState.update {
                    it.copy(
                        allCategoryExpense = list
                    )
                }
            }
        }
        viewModelScope.launch {
            categoryDataSource.getAllCategoryIncomeForEdit().collect { list ->
                _categoryEditUiState.update {
                    it.copy(
                        allCategoryIncome = list
                    )
                }
            }
        }
    }

    fun clickExpenseRecyclerView(position: Int) {
        viewModelScope.launch {
            if (categoryDataSource.isClickAddPositionExpense(position)) {
                categoryDataSource.cleanSelected()
                isSelect()
                type = TYPE_EXPENSE
                _isMoveAdd.value = true
                return@launch
            }

            categoryDataSource.selectExpense(position)
            isSelect()
        }
    }

    fun clickIncomeRecyclerView(position: Int) {
        viewModelScope.launch {
            if (categoryDataSource.isClickAddPositionIncome(position)) {
                categoryDataSource.cleanSelected()
                isSelect()
                type = TYPE_INCOME
                _isMoveAdd.value = true
                return@launch
            }

            categoryDataSource.selectIncome(position)
            isSelect()
        }
    }

    fun isSelect() {
        viewModelScope.launch {
            _isSelected.value = categoryDataSource.isSelect()
        }
    }

    fun removeSelectItem() {
        viewModelScope.launch {
            categoryDataSource.remove()
        }
    }

    fun clickBackButton() {
        _isReturn.value = true
    }

    fun moveAdd() {
        _isMoveAdd.value = false
    }

    fun getActionForMoveAdd(): NavDirections {
        return if (type == TYPE_EXPENSE)
            CategoryEditFragmentDirections
                .actionCategoryFragmentToCategoryAddFragment(TYPE_EXPENSE)
        else
            CategoryEditFragmentDirections
                .actionCategoryFragmentToCategoryAddFragment(TYPE_INCOME)
    }

    companion object {
        const val TYPE_EXPENSE = "expense"
        const val TYPE_INCOME = "income"
    }
}