package com.fiz.mono.ui.input

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentInputBinding
import com.fiz.mono.databinding.GridViewItemBinding
import com.google.android.material.tabs.TabLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    val viewModel: InputViewModel by activityViewModels()

    lateinit var adapter: CategoryInputAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.firstTime) {
            val action =
                InputFragmentDirections
                    .actionInputFragmentToOnBoardingFragment()
            view.findNavController().navigate(action)
            return
        }
        if (!viewModel.log) {
            val action =
                InputFragmentDirections
                    .actionInputFragmentToPINPasswordFragment()
            view.findNavController().navigate(action)
            return
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    when (tab.text){
                        "Expense"->{
                            val list = listOf(
                                CategoryItem("Bank", R.drawable.bank),
                                CategoryItem("Food", R.drawable.food),
                                CategoryItem("Medican", R.drawable.medican),
                                CategoryItem("Gym", R.drawable.gym),
                                CategoryItem("Coffee", R.drawable.coffee),
                                CategoryItem("Shopping", R.drawable.market),
                                CategoryItem("Cats", R.drawable.cat),
                                CategoryItem("Party", R.drawable.party),
                                CategoryItem("Gift", R.drawable.gift),
                                CategoryItem("Gas", R.drawable.gas),
                                CategoryItem("Edit", null),
                            )
                            adapter.submitList(list)
                        }
                        "Income"->{
                            val list = listOf(
                                CategoryItem("Freelance", R.drawable.challenge),
                                CategoryItem("Salary", R.drawable.money),
                                CategoryItem("Bonus", R.drawable.coin),
                                CategoryItem("Loan", R.drawable.user),
                                CategoryItem("Edit", null),
                            )
                            adapter.submitList(list)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.noteInputEditText.setEndIconOnClickListener {
            // Respond to end icon presses
        }

        adapter = CategoryInputAdapter()
        val list = listOf(
            CategoryItem("Bank", R.drawable.bank),
            CategoryItem("Food", R.drawable.food),
            CategoryItem("Medican", R.drawable.medican),
            CategoryItem("Gym", R.drawable.gym),
            CategoryItem("Coffee", R.drawable.coffee),
            CategoryItem("Shopping", R.drawable.market),
            CategoryItem("Cats", R.drawable.cat),
            CategoryItem("Party", R.drawable.party),
            CategoryItem("Gift", R.drawable.gift),
            CategoryItem("Gas", R.drawable.gas),
            CategoryItem("Edit", null),
        )
        adapter.submitList(list)
        binding.categoryInputRecyclerView.adapter = adapter

    }

    private fun isLogOut(): Boolean {
        return true
    }

    private fun isFirstTime(): Boolean {
        return true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InputFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InputFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

class CategoryInputAdapter :
    ListAdapter<CategoryItem, CategoryInputAdapter.CategoryItemViewHolder>(DiffCallback) {

    class CategoryItemViewHolder(
        private var binding: GridViewItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryItem: CategoryItem) {
            if (categoryItem.imgSrc!=null) {
                binding.iconImageView.setImageResource(
                        categoryItem.imgSrc
                )
                binding.descriptionTextView.text = categoryItem.name
            }else {
                binding.iconImageView.visibility = View.GONE
                binding.descriptionTextView.text = categoryItem.name
                binding.descriptionTextView.setTextColor( binding.root.resources.getColor(R.color.gray))
            }

            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
//            binding.executePendingBindings()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemViewHolder {
        return CategoryItemViewHolder(
            GridViewItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: CategoryItemViewHolder, position: Int) {
        val categoryPhoto = getItem(position)
        holder.bind(categoryPhoto)
    }

}

data class CategoryItem(
    val name: String,
    val imgSrc: Int?
)
