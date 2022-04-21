package com.fiz.mono.core.domain.models

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil

data class CategoryIcon(
    val id: String,
    @DrawableRes
    val imgSrc: Int,
    val selected: Boolean = false
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