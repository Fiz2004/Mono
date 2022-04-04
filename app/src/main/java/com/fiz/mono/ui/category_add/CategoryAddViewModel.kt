package com.fiz.mono.ui.category_add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.CategoryIcon
import com.fiz.mono.data.CategoryIconStore
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.ui.category_edit.CategoryEditViewModel.Companion.TYPE_EXPENSE
import kotlinx.coroutines.launch

class CategoryAddViewModel(
    private val categoryStore: CategoryStore,
    private val categoryIconStore: CategoryIconStore,

    ) : ViewModel() {
    private val _allCategoryIcon: MutableLiveData<MutableList<CategoryIcon>> =
        MutableLiveData(categoryIconStore.categoryIcons)
    val allCategoryIcon: LiveData<MutableList<CategoryIcon>> = _allCategoryIcon

    private val nameCategory: MutableLiveData<String> =
        MutableLiveData("")

    private val _isReturn = MutableLiveData(false)
    val isReturn: LiveData<Boolean> = _isReturn

    var type: String = TYPE_EXPENSE

    fun init(type: String) {
        this.type = type
        allCategoryIcon.value?.map { it.selected = false }
    }

    fun clickRecyclerView(position: Int) {
        categoryIconStore.select(position)
        _allCategoryIcon.value = allCategoryIcon.value!!
    }

    fun getVisibilityAddButton(): Boolean {
        return categoryIconStore.isSelected()
    }

    fun setCategoryName(text: CharSequence?) {
        nameCategory.value = text.toString()
    }

    fun clickAddButton() {
        if (nameCategory.value?.isNotBlank() != true) return
        if (categoryIconStore.isNotSelected()) return

        val name = nameCategory.value ?: return
        val selectedIcon = categoryIconStore.getSelectedIcon()
        viewModelScope.launch {
            categoryStore.addNewCategory(name, type, selectedIcon)
        }

        _isReturn.value = true
    }

    fun clickBackButton() {
        _isReturn.value = true
    }
}

