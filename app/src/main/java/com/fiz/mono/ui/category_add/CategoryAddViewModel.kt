package com.fiz.mono.ui.category_add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.CategoryIcon
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.categoryIcons
import com.fiz.mono.ui.category_edit.CategoryEditFragment
import kotlinx.coroutines.launch

class CategoryAddViewModel(
    private val categoryStore: CategoryStore
) : ViewModel() {
    private val _allCategoryIcon: MutableLiveData<MutableList<CategoryIcon>> =
        MutableLiveData(categoryIcons)
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
        if (!allCategoryIcon.value?.get(position)?.selected!!) {
            allCategoryIcon.value!!.find { it.selected }?.let {
                it.selected = false
            }
        }

        allCategoryIcon.value!![position].selected = !allCategoryIcon.value!![position].selected
        _allCategoryIcon.value = allCategoryIcon.value!!
    }

    fun getVisibilityAddButton(): Boolean {
        return isSelected()
    }

    private fun isSelected(): Boolean {
        return allCategoryIcon.value?.any { it.selected } ?: false
    }

    fun setCategoryName(text: CharSequence?) {
        _nameCategory.value = text.toString()
    }

    fun clickAddButton() {
        if (isSelected() && _nameCategory.value?.isNotBlank() == true) {

            val name = _nameCategory.value ?: return
            addNewCategory(name, type, getSelectedIcon())

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

    private fun getSelectedIcon(): String {
        return allCategoryIcon.value?.first { it.selected }?.id ?: ""
    }

    fun clickBackButton() {
        isReturn.value = true
    }
}

