package com.fiz.mono.ui.category_add

import android.view.View
import androidx.lifecycle.ViewModel
import com.fiz.mono.data.CategoryIconItem
import com.fiz.mono.data.CategoryStore

class CategoryAddViewModel : ViewModel() {
    private var selectedItem: Int? = null

    private val list = CategoryStore.getAllCategoryIcon()

    fun addSelectItem(position: Int) {
        if (selectedItem == position) {
            selectedItem = null
            list[position].selected = false
        } else {
            selectedItem?.let {
                list[it].selected = false
            }
            selectedItem = position
            list[position].selected = true
        }
    }

    fun getList(): List<CategoryIconItem> {
        return list.map { it.copy() }
    }

    fun getSelectedIcon(): Int {
        return list[selectedItem!!].imgSrc
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