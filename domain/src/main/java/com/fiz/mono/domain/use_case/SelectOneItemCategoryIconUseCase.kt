package com.fiz.mono.domain.use_case

import javax.inject.Inject

class SelectOneItemCategoryIconUseCase @Inject constructor() {
    operator fun <T : Selected<T>> invoke(
        baseList: List<T>,
        position: Int
    ): List<T> {
        return baseList.mapIndexed { index, T ->
            var selected = T.selected
            if (index != position && T.selected)
                selected = false
            if (index == position)
                selected = !selected
            T.copy(selected = selected)
        }
    }
}

interface Selected<T> {
    val selected: Boolean
    fun copy(selected: Boolean): T
}