package com.fiz.mono.ui.input

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.R
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.databinding.ItemCategoryBinding
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.setTextAppearanceCompat
import com.fiz.mono.util.themeColor

class CategoryInputAdapter(private val callback: (Int) -> Unit) :
    ListAdapter<CategoryItem, CategoryInputAdapter.CategoryItemViewHolder>(DiffCallback) {

    var selectedItem: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemViewHolder {
        return CategoryItemViewHolder(
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: CategoryItemViewHolder, position: Int) {
        val categoryItem = getItem(position)
        holder.bind(categoryItem, callback)
    }

    inner class CategoryItemViewHolder(
        private var binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryItem: CategoryItem, callback: (Int) -> Unit) {
            if (categoryItem.imgSrc != null) {
                binding.iconImageView.setImageResource(
                    categoryItem.imgSrc
                )
                binding.descriptionTextView.text = categoryItem.name

                binding.descriptionTextView.setTextAppearanceCompat(
                    binding.root.context,
                    R.style.size12_color_primary
                )
            } else {
                binding.iconImageView.visibility = View.GONE
                binding.descriptionTextView.text = categoryItem.name

                binding.descriptionTextView.setTextAppearanceCompat(
                    binding.root.context,
                    R.style.size12_color_secondary
                )
            }

            if (selectedItem == adapterPosition) {
                binding.cardMaterialCard.strokeColor =
                    binding.root.context.getColorCompat(R.color.red)
            } else {
                binding.cardMaterialCard.strokeColor =
                    binding.root.context.themeColor(R.attr.colorGray)
            }

            binding.root.setOnClickListener {
                callback(adapterPosition)
            }

        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem == newItem
        }
    }
}