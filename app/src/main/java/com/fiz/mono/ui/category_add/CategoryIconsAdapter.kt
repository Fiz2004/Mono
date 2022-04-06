package com.fiz.mono.ui.category_add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.databinding.ItemIconCategoryBinding
import com.fiz.mono.ui.models.CategoryIconItemDiff
import com.fiz.mono.ui.models.CategoryIconUiState

class CategoryIconsAdapter(private val clickCallback: (Int) -> Unit) :
    ListAdapter<CategoryIconUiState, CategoryIconsViewHolder>(CategoryIconItemDiff) {
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
    fun bind(categoryIcon: CategoryIconUiState, callback: (Int) -> Unit) {
        binding.apply {
            iconImageView.setImageResource(categoryIcon.imgSrc)
            cardMaterialCard.isActivated = categoryIcon.selected
            iconImageView.isActivated = categoryIcon.selected

            root.setOnClickListener { callback(adapterPosition) }
        }
    }
}