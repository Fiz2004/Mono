package com.fiz.mono.ui.category_add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.data_source.CategoryDataSource
import com.fiz.mono.data.data_source.CategoryIconDataSource
import com.fiz.mono.ui.category_edit.CategoryEditViewModel.Companion.TYPE_EXPENSE
import com.fiz.mono.ui.models.CategoryIconUiState
import kotlinx.coroutines.launch

class CategoryAddViewModel(
    private val categoryDataSource: CategoryDataSource,
    private val categoryIconDataSource: CategoryIconDataSource,

    ) : ViewModel() {
    private val _allCategoryIcon: MutableLiveData<MutableList<CategoryIconUiState>> =
        MutableLiveData(categoryIconDataSource.categoryIcons)
    val allCategoryIcon: LiveData<MutableList<CategoryIconUiState>> = _allCategoryIcon

    private val nameCategory: MutableLiveData<String> =
        MutableLiveData("")

    private val _isReturn = MutableLiveData(false)
    val isReturn: LiveData<Boolean> = _isReturn

    var type: String = TYPE_EXPENSE

    fun init(type: String) {
        this.type = type
        allCategoryIcon.value?.map { it.selectedFalse() }
    }

    fun clickRecyclerView(position: Int) {
        categoryIconDataSource.select(position)
        _allCategoryIcon.value = allCategoryIcon.value!!
    }

    fun getVisibilityAddButton(): Boolean {
        return categoryIconDataSource.isSelected()
    }

    fun setCategoryName(text: CharSequence?) {
        nameCategory.value = text.toString()
    }

    fun clickAddButton() {
        if (nameCategory.value?.isNotBlank() != true) return
        if (categoryIconDataSource.isNotSelected()) return

        val name = nameCategory.value ?: return
        val selectedIcon = categoryIconDataSource.getSelectedIcon()
        viewModelScope.launch {
            categoryDataSource.addNewCategory(name, type, selectedIcon)
        }

        _isReturn.value = true
    }

    fun clickBackButton() {
        _isReturn.value = true
    }
}

