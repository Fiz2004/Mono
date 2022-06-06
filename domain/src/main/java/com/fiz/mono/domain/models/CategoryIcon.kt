package com.fiz.mono.domain.models

import androidx.annotation.DrawableRes
import com.fiz.mono.domain.use_case.Selected

data class CategoryIcon(
    val id: String,
    @DrawableRes
    val imgSrc: Int,
    override val selected: Boolean = false
) : Selected<CategoryIcon> {
    override fun copySelected(selected: Boolean): CategoryIcon {
        return copy(selected = selected)
    }
}