package com.fiz.mono.ui.category_add

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.R
import com.fiz.mono.data.CategoryIcon
import com.fiz.mono.data.CategoryIconItemDiff
import com.fiz.mono.databinding.ItemIconCategoryBinding
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.themeColor

class CategoryIconsAdapter(private val clickCallback: (Int) -> Unit) :
    ListAdapter<CategoryIcon, CategoryIconsViewHolder>(CategoryIconItemDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryIconsViewHolder {
        val binding = ItemIconCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryIconsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryIconsViewHolder, position: Int) {
        val categoryIconItem = getItem(position)
        holder.bind(categoryIconItem, clickCallback)
    }
}

class CategoryIconsViewHolder(
    private var binding: ItemIconCategoryBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(categoryIcon: CategoryIcon, callback: (Int) -> Unit) {
        binding.apply {
            iconImageView.setImageResource(categoryIcon.imgSrc)
            cardMaterialCard.strokeColor = getStrokeColor(categoryIcon.selected)
            iconImageView.imageTintList = getImageTint(categoryIcon.selected)

            root.setOnClickListener { callback(adapterPosition) }
        }
    }

    private fun getStrokeColor(selected: Boolean): Int {
        return binding.root.context.run {
            if (selected)
                getColorCompat(R.color.blue)
            else
                themeColor(R.attr.colorGray)
        }
    }

    private fun getImageTint(selected: Boolean): ColorStateList {
        val color = getTintColor(selected)
        return ColorStateList.valueOf(color)
    }

    private fun getTintColor(selected: Boolean) = binding.root.context.run {
        if (selected)
            getColorCompat(R.color.blue)
        else
            themeColor(androidx.appcompat.R.attr.colorPrimary)
    }
}