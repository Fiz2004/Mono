package com.fiz.mono.domain.models

import androidx.recyclerview.widget.DiffUtil
import com.fiz.mono.domain.use_case.Selected

data class Category(
    val id: String,
    val name: String,
    val imgSrc: Int,
    override val selected: Boolean = false
) : Selected<Category> {
    override fun copySelected(selected: Boolean): Category {
        return copy(selected = selected)
    }
}

object CategoryItemDiff : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(
        oldItem: Category,
        newItem: Category
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Category,
        newItem: Category
    ): Boolean {
        return oldItem == newItem
    }
}