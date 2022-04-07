package com.fiz.mono.ui.category_add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.data_source.CategoryDataSource
import com.fiz.mono.data.data_source.CategoryIconUiStateDataSource
import com.fiz.mono.ui.category_edit.CategoryEditViewModel.Companion.TYPE_EXPENSE
import com.fiz.mono.ui.models.CategoryIconUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryAddUiState(
    val allCategoryIcons: List<CategoryIconUiState> = listOf(),
    val nameCategory: String = "",
    val isReturn: Boolean = false,
    val type: String = TYPE_EXPENSE
) {
    fun init(type: String): CategoryAddUiState {
        val allCategoryIcons = allCategoryIcons.map {
            it.copy(selected = false)
        }
        return copy(
            type = type,
            allCategoryIcons = allCategoryIcons
        )
    }

    fun clickRecyclerView(position: Int): CategoryAddUiState {
        val allCategoryIcons = allCategoryIcons.mapIndexed { index, categoryIconUiState ->
            var selected = categoryIconUiState.selected
            if (index != position && categoryIconUiState.selected)
                selected = false
            if (index == position)
                selected = !selected
            categoryIconUiState.copy(
                selected =
                selected
            )
        }
        return copy(
            allCategoryIcons = allCategoryIcons
        )
    }

    fun getVisibilityAddButton(): Boolean {
        return allCategoryIcons.any { it.selected } && nameCategory.isNotBlank()
    }

    fun setCategoryName(text: CharSequence?): CategoryAddUiState {
        return copy(nameCategory = text.toString())
    }
}

class CategoryAddViewModel(
    private val categoryDataSource: CategoryDataSource,
    private val categoryIconUiStateDataSource: CategoryIconUiStateDataSource,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryAddUiState())
    val uiState: StateFlow<CategoryAddUiState> = _uiState.asStateFlow()

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

    fun getVisibilityAddButton(): Boolean {
        return uiState.value.getVisibilityAddButton()
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
            categoryDataSource.addNewCategory(name, uiState.value.type, selectedIcon)
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

