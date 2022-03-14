package com.fiz.mono.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.R
import com.fiz.mono.databinding.ItemIconCategoryBinding
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.themeColor

class IconCategoryAdapter(private val callback: (Int) -> Unit) :
    ListAdapter<Int, IconCategoryAdapter.IconCategoryItemViewHolder>(DiffCallback) {

    var selectedItem: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconCategoryItemViewHolder {
        return IconCategoryItemViewHolder(
            ItemIconCategoryBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: IconCategoryItemViewHolder, position: Int) {
        val icon = getItem(position)
        holder.bind(icon, callback)
    }

    inner class IconCategoryItemViewHolder(
        private var binding: ItemIconCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(icon: Int, callback: (Int) -> Unit) {
            if (icon != null) {
                binding.iconImageView.setImageResource(
                    icon
                )
            } else {
                binding.iconImageView.visibility = View.GONE
            }

            if (selectedItem == adapterPosition) {
                binding.cardMaterialCard.strokeColor =
                    binding.root.context.getColorCompat(R.color.blue)
                binding.iconImageView.imageTintList =
                    ColorStateList.valueOf(binding.root.context.getColorCompat(R.color.blue))

            } else {
                binding.cardMaterialCard.strokeColor =
                    binding.root.context.themeColor(R.attr.colorGray)
                binding.iconImageView.imageTintList = ColorStateList.valueOf(
                    binding.root.context.themeColor(
                        androidx.appcompat.R.attr.colorPrimary
                    )
                )
            }

            binding.root.setOnClickListener {
                callback(adapterPosition)
            }

        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }
}