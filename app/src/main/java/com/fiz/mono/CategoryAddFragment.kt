package com.fiz.mono

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.databinding.FragmentCategoryAddBinding
import com.fiz.mono.databinding.ItemIconCategoryBinding

class CategoryAddFragment : Fragment() {
    private var _binding: FragmentCategoryAddBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: IconCategoryAdapter

    var selectedItem: Int? = null

    val list = mutableListOf(
        R.drawable.tennis_ball, R.drawable.boat, R.drawable.food, R.drawable.house
    )

    val args: CategoryAddFragmentArgs by navArgs()
    var type: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryAddBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = args.type

        adapter = IconCategoryAdapter { position ->

            selectedItem?.let { adapter.notifyItemChanged(it) }
            adapter.notifyItemChanged(position)
            if (selectedItem == position) {
                selectedItem = null
                binding.addButton.visibility = View.GONE
            } else {
                selectedItem = position
                binding.addButton.visibility = View.VISIBLE
            }

            adapter.selectedItem = selectedItem
        }
        adapter.submitList(list)
        binding.expenseRecyclerView.adapter = adapter

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addButton.setOnClickListener {
            if (selectedItem != null && binding.categoryNameEditText.text?.isNotBlank() == true) {

                val action =
                    CategoryAddFragmentDirections
                        .actionCategoryAddFragmentToCategoryFragment(
                            binding.categoryNameEditText.text.toString(),
                            list[selectedItem!!],
                            type
                        )
                view.findNavController().navigate(action)
                return@setOnClickListener
            }
        }

    }

}

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
                binding.cardMaterialCard.strokeColor = ContextCompat.getColor(binding.root.context, R.color.blue)
                ImageViewCompat.setImageTintList(
                    binding.iconImageView,
                    ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.blue))
                )

            } else {
                binding.cardMaterialCard.strokeColor = ContextCompat.getColor(binding.root.context, R.color.gray)
                ImageViewCompat.setImageTintList(
                    binding.iconImageView,
                    ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.primary))
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