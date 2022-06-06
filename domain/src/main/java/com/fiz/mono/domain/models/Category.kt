package com.fiz.mono.domain.models

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