package com.fiz.mono.data

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil

data class CategoryItem(
    val name: String,
    @DrawableRes val imgSrc: Int?,
    var selected: Boolean = false
)

object CategoryItemDiff : DiffUtil.ItemCallback<CategoryItem>() {
    override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
        return oldItem == newItem
    }
}