package com.fiz.mono.core.ui.category_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.fiz.mono.R
import com.fiz.mono.core.ui.shared_adapters.CategoriesAdapter
import com.fiz.mono.core.util.getColorCompat
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.databinding.FragmentCategoryEditBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.launch

@AndroidEntryPoint
@WithFragmentBindings
class CategoryEditFragment : Fragment() {
    private var _binding: FragmentCategoryEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryEditViewModel by viewModels()

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
        expenseAdapter = CategoriesAdapter(
            R.color.red
        ) { position ->
            viewModel.clickExpenseRecyclerView(position)
        }

        incomeAdapter = CategoriesAdapter(
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->

                    binding.navigationBarLayout.actionButton.setVisible(uiState.isSelected)

                    expenseAdapter.submitList(uiState.allCategoryExpense)
                    incomeAdapter.submitList(uiState.allCategoryIncome)

                    if (uiState.isMoveAdd) {
                        val action = viewModel.getActionForMoveAdd()
                        findNavController().navigate(action)
                        viewModel.moveAdd()
                    }

                    if (uiState.isReturn) {
                        findNavController().popBackStack()
                    }

                }
            }
        }
    }
}