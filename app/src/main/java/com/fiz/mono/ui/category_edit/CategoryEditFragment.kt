package com.fiz.mono.ui.category_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.App
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentCategoryEditBinding
import com.fiz.mono.ui.shared_adapters.CategoriesAdapter
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.setVisible

class CategoryEditFragment : Fragment() {
    private var _binding: FragmentCategoryEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryEditViewModel by viewModels {
        CategoryEditViewModelFactory(
            (requireActivity().application as App).categoryStore
        )
    }

    private lateinit var expenseAdapter: CategoriesAdapter
    private lateinit var incomeAdapter: CategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        bind()
        bindListener()
        subscribe()
    }

    private fun init() {
        // TODO Invent to Remove the transmission categoryIconStore
        expenseAdapter = CategoriesAdapter(
            (requireActivity().application as App).categoryIconStore,
            R.color.red
        ) { position ->
            viewModel.clickExpenseRecyclerView(position)
        }

        incomeAdapter = CategoriesAdapter(
            (requireActivity().application as App).categoryIconStore,
            R.color.red
        ) { position ->
            viewModel.clickIncomeRecyclerView(position)
        }
    }

    private fun bind() {
        binding.apply {
            navigationBarLayout.backButton.setVisible(true)
            navigationBarLayout.actionButton.setVisible(false)
            navigationBarLayout.actionButton.text = getString(R.string.remove)
            navigationBarLayout.actionButton.setTextColor(requireContext().getColorCompat(R.color.red))
            navigationBarLayout.choiceImageButton.setVisible(false)
            navigationBarLayout.titleTextView.text = getString(R.string.category_edit)
            expenseRecyclerView.adapter = expenseAdapter
            incomeRecyclerView.adapter = incomeAdapter
        }
    }

    private fun bindListener() {
        binding.apply {
            navigationBarLayout.backButton.setOnClickListener {
                viewModel.clickBackButton()
            }

            navigationBarLayout.actionButton.setOnClickListener {
                viewModel.removeSelectItem()
            }
        }
    }

    private fun subscribe() {
        viewModel.allCategoryExpense.observe(viewLifecycleOwner) { categoryItem ->
            expenseAdapter.submitList(categoryItem.map { it.copy() })
            binding.navigationBarLayout.actionButton.setVisible(viewModel.isSelect())
        }

        viewModel.allCategoryIncome.observe(viewLifecycleOwner) { categoryItem ->
            incomeAdapter.submitList(categoryItem.map { it.copy() })
            binding.navigationBarLayout.actionButton.setVisible(viewModel.isSelect())
        }

        // TODO Invent to Remove moveAdd()
        viewModel.isMoveAdd.observe(viewLifecycleOwner) {
            if (it) {
                val action = viewModel.getActionForMoveAdd()
                findNavController().navigate(action)
                viewModel.moveAdd()
            }
        }

        viewModel.isReturn.observe(viewLifecycleOwner) {
            if (it)
                findNavController().popBackStack()
        }
    }
}