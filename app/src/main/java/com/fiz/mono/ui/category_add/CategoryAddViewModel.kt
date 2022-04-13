package com.fiz.mono.ui.category_add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.data_source.CategoryIconUiStateDataSource
import com.fiz.mono.data.repositories.CategoryRepository
import com.fiz.mono.ui.category_edit.CategoryEditViewModel.Companion.TYPE_EXPENSE
import com.fiz.mono.ui.models.CategoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryAddViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val categoryIconUiStateDataSource: CategoryIconUiStateDataSource,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryAddUiState())
    val uiState: StateFlow<CategoryAddUiState> = _uiState.asStateFlow()

    private var allCategoryExpense: List<CategoryUiState> = listOf()
    private var allCategoryIncome: List<CategoryUiState> = listOf()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            categoryIconUiStateDataSource.allCategoryIcons.collect { allCategoryIcons ->
                _uiState.update {
                    it.copy(
                        allCategoryIcons = allCategoryIcons,
                    )
                }
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            categoryRepository.allCategoryExpense
                .distinctUntilChanged()
                .collect { allCategoryExpense ->
                    this@CategoryAddViewModel.allCategoryExpense = allCategoryExpense
                }
        }
        viewModelScope.launch(Dispatchers.Default) {
            categoryRepository.allCategoryIncome
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
                categoryRepository.insertNewCategoryExpense(newId, name, selectedIcon)
            } else {
                val numberLastItem = allCategoryIncome.lastOrNull()?.id?.substring(1)?.toInt()
                val newId = numberLastItem?.let { it + 1 } ?: 0
                categoryRepository.insertNewCategoryIncome(newId, name, selectedIcon)
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

