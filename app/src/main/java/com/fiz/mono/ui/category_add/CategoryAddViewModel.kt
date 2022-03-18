package com.fiz.mono.ui.category_add

import android.view.View
import androidx.lifecycle.ViewModel
import com.fiz.mono.data.CategoryIcon
import com.fiz.mono.data.categoryIcons

class CategoryAddViewModel : ViewModel() {
    private val allCategoryIcon = categoryIcons

    fun addSelectItem(position: Int) {
        if (!allCategoryIcon[position].selected) {
            val prevItems = allCategoryIcon.firstOrNull { it.selected }
            prevItems?.let {
                it.selected = false
            }
        }
        allCategoryIcon[position].selected = !allCategoryIcon[position].selected
    }

    fun getAllCategoryIcon(): List<CategoryIcon> {
        return allCategoryIcon.map { it.copy() }
    }

    fun getSelectedIcon(): Int {
        return allCategoryIcon.first { it.selected }.imgSrc
    }

    fun getVisibilityAddButton(): Int {
        return if (isSelected())
            View.VISIBLE
        else
            View.GONE
    }

    fun isSelected(): Boolean {
        return allCategoryIcon.any { it.selected }
    }
}