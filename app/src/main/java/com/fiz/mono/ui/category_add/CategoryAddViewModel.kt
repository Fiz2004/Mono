package com.fiz.mono.ui.category_add

import android.view.View
import androidx.lifecycle.ViewModel
import com.fiz.mono.data.CategoryIcon
import com.fiz.mono.data.categoryIcons

class CategoryAddViewModel : ViewModel() {
    private val allCategoryIcon = categoryIcons

    init {
        allCategoryIcon.forEach { it.selected = false }
    }

    fun addSelectItem(position: Int) {
        if (!allCategoryIcon[position].selected) {
            allCategoryIcon.find { it.selected }?.let {
                it.selected = false
            }
        }

        allCategoryIcon[position].selected = !allCategoryIcon[position].selected
    }

    fun getAllCategoryIcon(): List<CategoryIcon> {
        return allCategoryIcon.map { it.copy() }
    }

    fun getSelectedIcon(): String {
        return allCategoryIcon.first { it.selected }.id
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