package com.fiz.mono.category_add.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.category_add.domain.CategoryAddUseCase
import com.fiz.mono.domain.models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryAddViewModel @Inject constructor(
    private val categoryAddUseCase: CategoryAddUseCase
) : ViewModel() {

    var uiState = MutableStateFlow(CategoryAddUiState()); private set

    private var allCategoryExpense: List<Category> = listOf()
    private var allCategoryIncome: List<Category> = listOf()

    init {
        observeAllCategoriesExpense()
        observeAllCategoriesIncome()
        observeAllCategoryIcons()
    }

    private fun observeAllCategoriesExpense() {
        categoryAddUseCase.observeAllCategoriesExpenseUseCase()
            .onEach { allCategoryExpense ->
                this@CategoryAddViewModel.allCategoryExpense = allCategoryExpense
            }.launchIn(viewModelScope)
    }

    private fun observeAllCategoriesIncome() {
        categoryAddUseCase.observeAllCategoriesIncomeUseCase()
            .onEach { allCategoryIncome ->
                this@CategoryAddViewModel.allCategoryIncome = allCategoryIncome
            }.launchIn(viewModelScope)
    }

    private fun observeAllCategoryIcons() {
        categoryAddUseCase.observeAllCategoryIconsUseCase()
            .onEach { allCategoryIcons ->
                uiState.value = uiState.value.copy(allCategoryIcons = allCategoryIcons)
            }.launchIn(viewModelScope)
    }

    fun onEvent(event: FeatureAddUIEvent) {
        when (event) {
            is FeatureAddUIEvent.ChangeCategoryName -> setCategoryName(event.newCategoryName)
            FeatureAddUIEvent.ClickAddButton -> clickAddButton()
            FeatureAddUIEvent.ClickBackButton -> clickBackButton()
            is FeatureAddUIEvent.ClickCategory -> clickRecyclerView(event.position)
            FeatureAddUIEvent.OnClickBackButton -> onClickBackButton()
            is FeatureAddUIEvent.Loading -> start(event.type)
        }
    }

    private fun start(type: String) {
        uiState.value = uiState.value.copy(type = type)
    }

    private fun clickRecyclerView(position: Int) {
        val allCategoryIcons =
            categoryAddUseCase.selectOneItemCategoryIconUseCase(
                uiState.value.allCategoryIcons,
                position
            )
        uiState.value = uiState.value.copy(
            allCategoryIcons = allCategoryIcons
        )
    }

    private fun setCategoryName(newCategoryName: String) {
        uiState.value = uiState.value.copy(nameCategory = newCategoryName)
    }

    private fun clickAddButton() {
        viewModelScope.launch {
            val name = uiState.value.nameCategory
            val selectedIcon = uiState.value.allCategoryIcons.first { it.selected }.id
            categoryAddUseCase.insertNewCategoryUseCase(uiState.value.type, name, selectedIcon)
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

