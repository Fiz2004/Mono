package com.fiz.mono.ui.category_add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.data_source.CategoryDataSource
import com.fiz.mono.data.data_source.CategoryIconUiStateDataSource
import com.fiz.mono.ui.category_edit.CategoryEditViewModel.Companion.TYPE_EXPENSE
import com.fiz.mono.ui.models.CategoryUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoryAddViewModel(
    private val categoryDataSource: CategoryDataSource,
    private val categoryIconUiStateDataSource: CategoryIconUiStateDataSource,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryAddUiState())
    val uiState: StateFlow<CategoryAddUiState> = _uiState.asStateFlow()

    private var allCategoryExpense: List<CategoryUiState> = listOf()
    private var allCategoryIncome: List<CategoryUiState> = listOf()

    init {
        viewModelScope.launch {
            categoryIconUiStateDataSource.allCategoryIcons.collect { allCategoryIcons ->
                _uiState.update {
                    it.copy(
                        allCategoryIcons = allCategoryIcons,
                    )
                }
            }
        }
        viewModelScope.launch {
            categoryDataSource.allCategoryExpense
                .distinctUntilChanged()
                .collect { allCategoryExpense ->
                    this@CategoryAddViewModel.allCategoryExpense = allCategoryExpense
                }
        }
        viewModelScope.launch {
            categoryDataSource.allCategoryIncome
                .distinctUntilChanged()
                .collect { allCategoryIncome ->
                    this@CategoryAddViewModel.allCategoryIncome = allCategoryIncome
                }
        }
    }

    fun init(type: String) {
        _uiState.update {
            it.init(type)
        }
    }

    fun clickRecyclerView(position: Int) {
        _uiState.update {
            it.clickRecyclerView(position)
        }
    }

    fun setCategoryName(text: CharSequence?) {
        _uiState.update {
            it.setCategoryName(text)
        }
    }

    fun clickAddButton() {
        val name = uiState.value.nameCategory
        val selectedIcon = uiState.value.allCategoryIcons.first { it.selected }.id
        viewModelScope.launch {
            if (uiState.value.type == TYPE_EXPENSE) {
                val numberLastItem = allCategoryExpense.lastOrNull()?.id?.substring(1)?.toInt()
                val newId = numberLastItem?.let { it + 1 } ?: 0
                categoryDataSource.insertNewCategoryExpense(newId, name, selectedIcon)
            } else {
                val numberLastItem = allCategoryIncome.lastOrNull()?.id?.substring(1)?.toInt()
                val newId = numberLastItem?.let { it + 1 } ?: 0
                categoryDataSource.insertNewCategoryIncome(newId, name, selectedIcon)
            }
        }

        _uiState.update {
            it.copy(isReturn = true)
        }
    }

    fun clickBackButton() {
        _uiState.update {
            it.copy(isReturn = true)
        }
    }
}

