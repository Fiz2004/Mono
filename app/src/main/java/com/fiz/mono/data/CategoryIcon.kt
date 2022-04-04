package com.fiz.mono.data

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil

data class CategoryIcon(
    val id: String,
    @DrawableRes
    val imgSrc: Int,
    var selected: Boolean = false
)

object CategoryIconItemDiff : DiffUtil.ItemCallback<CategoryIcon>() {
    override fun areItemsTheSame(
        oldItem: CategoryIcon,
        newItem: CategoryIcon
    ): Boolean {
        return oldItem.imgSrc == newItem.imgSrc
    }

    override fun areContentsTheSame(
        oldItem: CategoryIcon,
        newItem: CategoryIcon
    ): Boolean {
        return oldItem == newItem
    }
}