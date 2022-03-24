package com.fiz.mono.ui.shared_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.R
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.CategoryItemDiff
import com.fiz.mono.data.getDrawableCategoryIcon
import com.fiz.mono.databinding.ItemCategoryBinding
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.setTextAppearanceCompat
import com.fiz.mono.util.setVisible
import com.fiz.mono.util.themeColor

class CategoriesAdapter(private val colorSelected: Int, private val callback: (Int) -> Unit) :
    ListAdapter<CategoryItem, CategoriesViewHolder>(CategoryItemDiff) {
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
    fun bind(categoryItem: CategoryItem, colorSelected: Int, callback: (Int) -> Unit) {
        binding.apply {
            iconImageView.setVisible(categoryItem.mapImgSrc != "")
            if (categoryItem.mapImgSrc != "")
                iconImageView.setImageResource(getDrawableCategoryIcon(categoryItem.mapImgSrc))

            descriptionTextView.text = categoryItem.name
            descriptionTextView.setTextAppearanceCompat(
                root.context,
                R.style.size12_color_primary
            )
            descriptionTextView.setTextColor(getColorText(categoryItem.mapImgSrc))
            cardMaterialCard.strokeColor = getStrokeColor(categoryItem.selected, colorSelected)

            root.setOnClickListener { callback(adapterPosition) }
        }
    }

    private fun getColorText(imgSrc: String): Int {
        return if (imgSrc != "")
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