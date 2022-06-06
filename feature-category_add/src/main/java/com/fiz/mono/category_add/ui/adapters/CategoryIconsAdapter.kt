package com.fiz.mono.category_add.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.category_add.databinding.ItemIconCategoryBinding
import com.fiz.mono.domain.models.CategoryIcon

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
            cardMaterialCard.isActivated = categoryIcon.selected
            iconImageView.isActivated = categoryIcon.selected

            root.setOnClickListener { callback(adapterPosition) }
        }
    }
}


object CategoryIconItemDiff : DiffUtil.ItemCallback<CategoryIcon>() {
    override fun areItemsTheSame(
        oldItem: CategoryIcon,
        newItem: CategoryIcon
    ): Boolean {
        return oldItem.imgSrc == newItem.imgSrc
    }

    override fun areContentsTheSame(
        oldItem: CategoryIcon,
        newItem: CategoryIcon
    ): Boolean {
        return oldItem == newItem
    }
}