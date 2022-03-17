package com.fiz.mono.data

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil

data class CategoryIconItem(
    @DrawableRes
    val imgSrc: Int,
    var selected: Boolean = false
) {
    companion object DiffCallback : DiffUtil.ItemCallback<CategoryIconItem>() {
        override fun areItemsTheSame(
            oldItem: CategoryIconItem,
            newItem: CategoryIconItem
        ): Boolean {
            return oldItem.imgSrc == newItem.imgSrc
        }

        override fun areContentsTheSame(
            oldItem: CategoryIconItem,
            newItem: CategoryIconItem
        ): Boolean {
            return oldItem.selected == newItem.selected
        }
    }
}