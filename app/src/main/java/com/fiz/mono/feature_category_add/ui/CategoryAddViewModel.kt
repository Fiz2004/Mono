package com.fiz.mono.feature_category_add.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.core.data.data_source.CategoryIconUiStateDataSource
import com.fiz.mono.core.domain.models.Category
import com.fiz.mono.core.domain.repositories.CategoryRepository
import com.fiz.mono.core.ui.category_edit.CategoryEditViewModel.Companion.TYPE_EXPENSE
import com.fiz.mono.feature_category_add.domain.use_case.SelectOneItemCategoryIconUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryAddViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val categoryIconUiStateDataSource: CategoryIconUiStateDataSource,
    private val selectOneItemCategoryIconUseCase: SelectOneItemCategoryIconUseCase
) : ViewModel() {

    var uiState = MutableStateFlow(CategoryAddUiState()); private set

    private var allCategoryExpense: List<Category> = listOf()
    private var allCategoryIncome: List<Category> = listOf()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            launch {
                categoryIconUiStateDataSource.allCategoryIcons
                    .collect { allCategoryIcons ->
                        uiState.value = uiState.value.copy(allCategoryIcons = allCategoryIcons)
                    }
            }

            launch {
                categoryRepository.allCategoryExpense
                    .collect { allCategoryExpense ->
                        this@CategoryAddViewModel.allCategoryExpense = allCategoryExpense
                    }
            }

            launch {
                categoryRepository.allCategoryIncome
                    .collect { allCategoryIncome ->
                        this@CategoryAddViewModel.allCategoryIncome = allCategoryIncome
                    }
            }
        }
    }

    fun onEvent(event: FeatureAddUIEvent) {
        when (event) {
            is FeatureAddUIEvent.ChangeCategoryName -> setCategoryName(event.newCategoryName)
            FeatureAddUIEvent.ClickAddButton -> clickAddButton()
            FeatureAddUIEvent.ClickBackButton -> clickBackButton()
            is FeatureAddUIEvent.ClickCategory -> clickRecyclerView(event.position)
            FeatureAddUIEvent.OnClickBackButton -> onClickBackButton()
            is FeatureAddUIEvent.Loading -> init(event.type)
        }
    }

    private fun init(type: String) {
        uiState.value = uiState.value.copy(type = type)
    }

    private fun clickRecyclerView(position: Int) {
        val allCategoryIcons =
            selectOneItemCategoryIconUseCase(uiState.value.allCategoryIcons, position)
        uiState.value = uiState.value.copy(
            allCategoryIcons = allCategoryIcons
        )
    }

    private fun setCategoryName(newCategoryName: String) {
        uiState.value = uiState.value.copy(nameCategory = newCategoryName.toString())

    }

    private fun clickAddButton() {
        viewModelScope.launch {
            val name = uiState.value.nameCategory
            val selectedIcon = uiState.value.allCategoryIcons.first { it.selected }.id
            if (uiState.value.type == TYPE_EXPENSE) {
                categoryRepository.insertNewCategoryExpense(name, selectedIcon)
            } else {
                categoryRepository.insertNewCategoryIncome(name, selectedIcon)
            }
            uiState.value = uiState.value.copy(isReturn = true)
        }
    }

    private fun clickBackButton() {
        uiState.value = uiState.value.copy(isReturn = true)
    }

    private fun onClickBackButton() {
        uiState.value = uiState.value.copy(isReturn = false)
    }
}

