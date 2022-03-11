package com.fiz.mono.ui.settings

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.R
import com.fiz.mono.databinding.OptionsSettingsItemBinding


class SettingsAdapter :
    ListAdapter<SettingsItem, SettingsAdapter.SettingsItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsItemViewHolder {
        return SettingsItemViewHolder(
            OptionsSettingsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SettingsItemViewHolder, position: Int) {
        val settingsItem = getItem(position)
        holder.bind(settingsItem)
    }

    class SettingsItemViewHolder(
        private var binding: OptionsSettingsItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(settingsItem: SettingsItem) {
            binding.iconCategoryImageView.setImageResource(settingsItem.iconSrc)

            binding.nameCategoryTextView.text = settingsItem.name

            binding.carretCircleRightImageView.visibility = when (settingsItem.lastElement) {
                LastElement.NONE -> View.GONE
                LastElement.ARROW -> View.VISIBLE
                LastElement.SWITCH -> View.GONE
            }

            binding.lightDarkModeSwitch.visibility = when (settingsItem.lastElement) {
                LastElement.NONE -> View.GONE
                LastElement.ARROW -> View.GONE
                LastElement.SWITCH -> View.VISIBLE
            }

            val color = when (settingsItem.color) {
                Color.BLACK -> {
                    val typedValue = TypedValue()
                    binding.root.context.theme.resolveAttribute(
                        androidx.appcompat.R.attr.colorPrimary,
                        typedValue,
                        true
                    )
                    typedValue.data
                }
                else -> {
                    Color.RED
                }
            }
            binding.nameCategoryTextView.setTextColor(color)

            if (settingsItem.name == "Dark mode") {
                val margin24inDp = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 24F, binding.root.resources.displayMetrics
                )
                val layoutParams = binding.root.layoutParams as RecyclerView.LayoutParams
                layoutParams.setMargins(
                    layoutParams.marginStart,
                    margin24inDp.toInt(),
                    layoutParams.marginEnd,
                    layoutParams.bottomMargin
                )
                binding.root.layoutParams = layoutParams
            } else {
                val margin4inDp = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 4F, binding.root.resources.displayMetrics
                )
                val layoutParams = binding.root.layoutParams as RecyclerView.LayoutParams
                layoutParams.setMargins(
                    layoutParams.marginStart,
                    margin4inDp.toInt(), layoutParams.marginEnd, layoutParams.bottomMargin
                )
                binding.root.layoutParams = layoutParams
            }

            binding.lightDarkModeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }

    companion object {

        fun createOptions(): List<SettingsItem> {
            return listOf(
                SettingsItem("Category", R.drawable.squares_four, LastElement.ARROW, Color.BLACK),
                SettingsItem(
                    "Currency",
                    R.drawable.currency_circle,
                    LastElement.ARROW,
                    Color.BLACK
                ),
                SettingsItem("PIN password", R.drawable.lock_key, LastElement.ARROW, Color.BLACK),
                SettingsItem(
                    "Reminder",
                    R.drawable.bell_simple_ringing,
                    LastElement.ARROW,
                    Color.BLACK
                ),
                SettingsItem("Dark mode", R.drawable.mode, LastElement.SWITCH, Color.BLACK),
                SettingsItem("Delete All Data", R.drawable.trash, LastElement.NONE, Color.RED),
            )
        }

        object DiffCallback : DiffUtil.ItemCallback<SettingsItem>() {
            override fun areItemsTheSame(oldItem: SettingsItem, newItem: SettingsItem): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: SettingsItem, newItem: SettingsItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}




