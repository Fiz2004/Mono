package com.fiz.mono.ui.category_add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fiz.mono.databinding.FragmentCategoryAddBinding

class CategoryAddFragment : Fragment() {
    private var _binding: FragmentCategoryAddBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryAddViewModel by viewModels()

    private lateinit var adapter: IconCategoryAdapter

    private val args: CategoryAddFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener(::backButtonOnClickListener)

        binding.addButton.setOnClickListener(::addButtonOnClickListener)

        adapter = IconCategoryAdapter(::adapterOnClickListener)
        adapter.submitList(viewModel.getAllCategoryIcon())
        binding.expenseRecyclerView.adapter = adapter
    }

    private fun backButtonOnClickListener(v: View): Unit {
        findNavController().popBackStack()
    }

    private fun addButtonOnClickListener(view: View): Unit {
        if (viewModel.isSelected() && binding.categoryNameEditText.text?.isNotBlank() == true) {
            val name = binding.categoryNameEditText.text.toString()
            val action =
                CategoryAddFragmentDirections
                    .actionCategoryAddFragmentToCategoryFragment(
                        name,
                        viewModel.getSelectedIcon(),
                        args.type
                    )
            view.findNavController().navigate(action)
            return
        }
    }

    private fun adapterOnClickListener(position: Int) {
        viewModel.addSelectItem(position)
        binding.addButton.visibility = viewModel.getVisibilityAddButton()
        adapter.submitList(viewModel.getAllCategoryIcon())
    }

}