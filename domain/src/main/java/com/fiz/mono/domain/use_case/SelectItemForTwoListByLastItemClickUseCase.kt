package com.fiz.mono.domain.use_case

import com.fiz.mono.domain.models.Category
import javax.inject.Inject

class SelectItemForTwoListByLastItemClickUseCase @Inject constructor(
    val selectOneItemCategoryIconUseCase: SelectOneItemCategoryIconUseCase
) {

    fun getNewClickList(clickList: List<Category>, position: Int): List<Category> {
        return if (position == clickList.size - 1)
            clickList.map { it.copy(selected = false) }
        else
            selectOneItemCategoryIconUseCase(clickList, position)
    }

    fun getOtherList(
        clickList: List<Category>,
        otherList: List<Category>,
        position: Int
    ): List<Category> {
        return if (position == clickList.size - 1) {
            otherList.map { it.copy(selected = false) }
        } else {
            otherList.map {
                var selected = it.selected
                if (it.selected)
                    selected = false
                it.copy(selected = selected)
            }
        }
    }
}