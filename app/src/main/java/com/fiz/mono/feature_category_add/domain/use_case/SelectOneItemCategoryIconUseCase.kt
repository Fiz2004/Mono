package com.fiz.mono.feature_category_add.domain.use_case

import com.fiz.mono.core.domain.models.CategoryIcon
import javax.inject.Inject

class SelectOneItemCategoryIconUseCase @Inject constructor() {
    operator fun invoke(
        baseList: List<CategoryIcon>,
        position: Int
    ): List<CategoryIcon> {
        return baseList.mapIndexed { index, CategoryIcon ->
            var selected = CategoryIcon.selected
            if (index != position && CategoryIcon.selected)
                selected = false
            if (index == position)
                selected = !selected
            CategoryIcon.copy(
                selected =
                selected
            )
        }
    }
}