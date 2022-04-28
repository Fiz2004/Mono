package com.fiz.mono.input.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.util.getColorCompat
import com.fiz.mono.core.util.setTextAppearanceCompat
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.core.util.themeColor
import com.fiz.mono.domain.models.Category
import com.fiz.mono.domain.models.CategoryItemDiff
import com.fiz.mono.feature.input.databinding.ItemCategoryBinding

class CategoriesAdapter(
    private val colorSelected: Int,
    private val callback: (Int) -> Unit
) :
    ListAdapter<Category, CategoriesViewHolder>(CategoryItemDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val categoryItem = getItem(position)
        holder.bind(categoryItem, colorSelected, callback)
    }
}

class CategoriesViewHolder(
    private var binding: ItemCategoryBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        category: Category,
        colorSelected: Int,
        callback: (Int) -> Unit
    ) {
        binding.apply {
            iconImageView.setVisible(category.imgSrc != 0)
            if (category.imgSrc != 0)
                iconImageView.setImageResource(category.imgSrc)

            descriptionTextView.text = category.name
            descriptionTextView.setTextAppearanceCompat(
                root.context,
                R.style.size12_color_primary
            )
            descriptionTextView.setTextColor(getColorText(category.imgSrc))
            cardMaterialCard.strokeColor = getStrokeColor(category.selected, colorSelected)

            root.setOnClickListener { callback(adapterPosition) }
        }
    }

    private fun getColorText(imgSrc: Int): Int {
        return if (imgSrc != 0)
            binding.root.context.themeColor(com.google.android.material.R.attr.colorPrimary)
        else
            binding.root.context.themeColor(com.google.android.material.R.attr.colorSecondary)
    }

    private fun getStrokeColor(selected: Boolean, colorSelected: Int): Int {
        return binding.root.context.run {
            if (selected)
                getColorCompat(colorSelected)
            else
                themeColor(R.attr.colorGray)
        }
    }
}