package com.fiz.mono.ui.category_add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.CategoryIcon
import com.fiz.mono.data.CategoryIconStore
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.ui.category_edit.CategoryEditFragment
import kotlinx.coroutines.launch

class CategoryAddViewModel(
    private val categoryStore: CategoryStore,
    private val categoryIconStore: CategoryIconStore,

    ) : ViewModel() {
    private val _allCategoryIcon: MutableLiveData<MutableList<CategoryIcon>> =
        MutableLiveData(categoryIconStore.categoryIcons)
    val allCategoryIcon: LiveData<MutableList<CategoryIcon>> = _allCategoryIcon

    private val _nameCategory: MutableLiveData<String> =
        MutableLiveData("")

    val isReturn = MutableLiveData(false)

    var type: String = CategoryEditFragment.TYPE_EXPENSE

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
        _nameCategory.value = text.toString()
    }

    fun clickAddButton() {
        if (categoryIconStore.isSelected() && _nameCategory.value?.isNotBlank() == true) {

            val name = _nameCategory.value ?: return
            val selectedIcon = categoryIconStore.getSelectedIcon()
            addNewCategory(name, type, selectedIcon)

            isReturn.value = true
        }
    }

    private fun addNewCategory(name: String, type: String, selectedIcon: String) {
        viewModelScope.launch {
            if (type == CategoryEditFragment.TYPE_EXPENSE) {
                categoryStore.insertNewCategoryExpense(name, selectedIcon)
            } else {
                categoryStore.insertNewCategoryIncome(name, selectedIcon)
            }
        }
    }

    fun clickBackButton() {
        isReturn.value = true
    }
}

