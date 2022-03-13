package com.fiz.mono.ui.input

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.R
import com.fiz.mono.databinding.GridViewItemBinding

class CategoryInputAdapter(private val callback: (Int) -> Unit) :
    ListAdapter<CategoryItem, CategoryInputAdapter.CategoryItemViewHolder>(DiffCallback) {

    var selectedItem: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemViewHolder {
        return CategoryItemViewHolder(
            GridViewItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: CategoryItemViewHolder, position: Int) {
        val categoryItem = getItem(position)
        holder.bind(categoryItem, callback)
    }

    inner class CategoryItemViewHolder(
        private var binding: GridViewItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryItem: CategoryItem, callback: (Int) -> Unit) {
            if (categoryItem.imgSrc != null) {
                binding.iconImageView.setImageResource(
                    categoryItem.imgSrc
                )
                binding.descriptionTextView.text = categoryItem.name

                setTextAppearanceForDescriptionTextView(R.style.size12_color_primary)
            } else {
                binding.iconImageView.visibility = View.GONE
                binding.descriptionTextView.text = categoryItem.name

                setTextAppearanceForDescriptionTextView(R.style.size12_color_secondary)
            }

            if (selectedItem == adapterPosition) {
                binding.cardMaterialCard.strokeColor = Color.RED
            } else {
                binding.cardMaterialCard.strokeColor = binding.root.resources.getColor(R.color.gray)
            }

            binding.root.setOnClickListener {
                callback(adapterPosition)
            }

        }

        private fun setTextAppearanceForDescriptionTextView(textAppearance: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.descriptionTextView.setTextAppearance(textAppearance)
            } else {
                binding.descriptionTextView.setTextAppearance(binding.root.context, textAppearance)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem == newItem
        }
    }
}