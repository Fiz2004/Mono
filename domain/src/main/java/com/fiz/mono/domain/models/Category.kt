package com.fiz.mono.domain.models

import androidx.recyclerview.widget.DiffUtil
import com.fiz.mono.domain.use_case.Selected

data class Category(
    val id: String,
    val name: String,
    val imgSrc: Int,
    override val selected: Boolean = false
) : Selected<Category> {
    override fun copy(selected: Boolean): Category {
        return copy(selected = selected)
    }
}

object CategoryItemDiff : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(
        old: Category,
        aNew: Category
    ): Boolean {
        return old.name == aNew.name
    }

    override fun areContentsTheSame(
        old: Category,
        aNew: Category
    ): Boolean {
        return old == aNew
    }
}