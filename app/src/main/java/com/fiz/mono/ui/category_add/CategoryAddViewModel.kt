package com.fiz.mono.ui.category_add

import android.view.View
import androidx.lifecycle.ViewModel
import com.fiz.mono.data.CategoryIconItem
import com.fiz.mono.data.CategoryStore

class CategoryAddViewModel : ViewModel() {
    private var selectedItem: Int? = null

    private val allCategoryIcon = CategoryStore.getAllCategoryIcon()

    fun addSelectItem(position: Int) {
        if (selectedItem == position) {
            selectedItem = null
            allCategoryIcon[position].selected = false
        } else {
            selectedItem?.let {
                allCategoryIcon[it].selected = false
            }
            selectedItem = position
            allCategoryIcon[position].selected = true
        }
    }

    fun getAllCategoryIcon(): List<CategoryIconItem> {
        return allCategoryIcon.map { it.copy() }
    }

    fun getSelectedIcon(): Int {
        return allCategoryIcon[selectedItem!!].imgSrc
    }

    fun getVisibilityAddButton(): Int {
        return if (isSelected())
            View.VISIBLE
        else
            View.GONE
    }

    fun isSelected(): Boolean {
        return selectedItem != null
    }
}