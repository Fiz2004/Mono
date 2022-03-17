package com.fiz.mono.ui.category_add

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.R
import com.fiz.mono.data.CategoryIconItem
import com.fiz.mono.databinding.ItemIconCategoryBinding
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.themeColor

class IconCategoryAdapter(private val clickCallback: (Int) -> Unit) :
    ListAdapter<CategoryIconItem, IconCategoryAdapter.IconCategoryItemViewHolder>(CategoryIconItem.DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconCategoryItemViewHolder {
        return IconCategoryItemViewHolder(
            ItemIconCategoryBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: IconCategoryItemViewHolder, position: Int) {
        val categoryIconItem = getItem(position)
        holder.bind(categoryIconItem, clickCallback)
    }

    inner class IconCategoryItemViewHolder(
        private var binding: ItemIconCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryIconItem: CategoryIconItem, callback: (Int) -> Unit) {
            binding.iconImageView.setImageResource(categoryIconItem.imgSrc)
            binding.cardMaterialCard.strokeColor = getStrokeColor(categoryIconItem.selected)
            binding.iconImageView.imageTintList = getImageTint(categoryIconItem.selected)

            binding.root.setOnClickListener { callback(adapterPosition) }
        }

        private fun getStrokeColor(selected: Boolean): Int {
            return if (selected)
                binding.root.context.getColorCompat(R.color.blue)
            else
                binding.root.context.themeColor(R.attr.colorGray)
        }

        private fun getImageTint(selected: Boolean): ColorStateList {
            val color = getTintColor(selected)
            return ColorStateList.valueOf(color)
        }

        private fun getTintColor(selected: Boolean) = if (selected)
            binding.root.context.getColorCompat(R.color.blue)
        else
            binding.root.context.themeColor(androidx.appcompat.R.attr.colorPrimary)
    }
}