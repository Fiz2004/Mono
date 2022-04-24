package com.fiz.mono.core.domain.models

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import com.fiz.mono.feature_category_add.domain.use_case.Selected

data class CategoryIcon(
    val id: String,
    @DrawableRes
    val imgSrc: Int,
    override val selected: Boolean = false
) : Selected<CategoryIcon> {
    override fun copy(selected: Boolean): CategoryIcon {
        return copy(selected = selected)
    }
}

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