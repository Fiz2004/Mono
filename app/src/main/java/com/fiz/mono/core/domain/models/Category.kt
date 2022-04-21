package com.fiz.mono.core.domain.models

import androidx.recyclerview.widget.DiffUtil

data class Category(
    val id: String,
    val name: String,
    val imgSrc: Int,
    val selected: Boolean = false
)

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