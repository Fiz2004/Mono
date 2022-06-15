package com.fiz.mono.category_edit.ui

sealed class CategoryEditViewEffect {
    object MoveReturn : CategoryEditViewEffect()
    object MoveCategoryAdd : CategoryEditViewEffect()
}