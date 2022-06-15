package com.fiz.mono.category_add.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.category_add.domain.CategoryAddUseCase
import com.fiz.mono.domain.models.Category
import com.fiz.mono.domain.models.TypeTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryAddViewModel @Inject constructor(
    private val categoryAddUseCase: CategoryAddUseCase
) : ViewModel() {

    var viewState = MutableStateFlow(CategoryAddViewState())
        private set

    var viewEffects = MutableSharedFlow<CategoryAddViewEffect>()
        private set

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
        viewState.value =
            viewState.value.copy(allCategoryIcons = categoryAddUseCase.observeAllCategoryIconsUseCase())
    }

    fun onEvent(event: FeatureAddEvent) {
        when (event) {
            is FeatureAddEvent.CategoryNameChanged -> setCategoryName(event.newCategoryName)
            FeatureAddEvent.AddButtonClicked -> clickAddButton()
            FeatureAddEvent.BackButtonClicked -> clickBackButton()
            is FeatureAddEvent.CategoryClicked -> clickRecyclerView(event.position)
            is FeatureAddEvent.ActivityLoaded -> start(event.type)
        }
    }

    private fun start(type: TypeTransaction) {
        viewState.value = viewState.value.copy(type = type)
    }

    private fun clickRecyclerView(position: Int) {
        val allCategoryIcons =
            categoryAddUseCase.selectOneItemCategoryIconUseCase(
                viewState.value.allCategoryIcons,
                position
            )
        viewState.value = viewState.value.copy(
            allCategoryIcons = allCategoryIcons
        )
    }

    private fun setCategoryName(newCategoryName: String) {
        viewState.value = viewState.value.copy(nameCategory = newCategoryName)
    }

    private fun clickAddButton() {
        viewModelScope.launch {
            val name = viewState.value.nameCategory
            val selectedIcon = viewState.value.allCategoryIcons.first { it.selected }.id
            categoryAddUseCase.insertNewCategoryUseCase(viewState.value.type, name, selectedIcon)

            viewEffects.emit(CategoryAddViewEffect.MoveReturn)
        }
    }

    private fun clickBackButton() {
        viewModelScope.launch {
            viewEffects.emit(CategoryAddViewEffect.MoveReturn)
        }
    }
}

