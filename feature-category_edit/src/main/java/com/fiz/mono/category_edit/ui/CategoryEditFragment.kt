package com.fiz.mono.category_edit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.base.android.adapters.CategoriesAdapter
import com.fiz.mono.category_edit.databinding.FragmentCategoryEditBinding
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.util.getColorCompat
import com.fiz.mono.core.util.launchAndRepeatWithViewLifecycle
import com.fiz.mono.core.util.setVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryEditFragment : Fragment() {

    private val viewModel: CategoryEditViewModel by viewModels()

    private val expenseAdapter: CategoriesAdapter by lazy {
        CategoriesAdapter(
            R.color.red
        ) { position ->
            viewModel.onEvent(CategoryEditUiEvent.ClickExpenseItem(position))
        }
    }

    private val incomeAdapter: CategoriesAdapter by lazy {
        CategoriesAdapter(
            R.color.red
        ) { position ->
            viewModel.onEvent(CategoryEditUiEvent.ClickIncomeItem(position))
        }
    }

    private lateinit var binding: FragmentCategoryEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind()
        bindListener()
        subscribe()

        setupNavigation()
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
                viewModel.onEvent(CategoryEditUiEvent.ClickBackButton)
            }

            navigationBarLayout.actionButton.setOnClickListener {
                viewModel.onEvent(CategoryEditUiEvent.ClickRemoveButton)
            }
        }
    }

    private fun subscribe() {
        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collect { uiState ->

                binding.navigationBarLayout.actionButton.setVisible(uiState.isRemoveButtonVisible)

                expenseAdapter.submitList(uiState.allCategoryExpense)
                incomeAdapter.submitList(uiState.allCategoryIncome)

            }
        }
    }

    private fun setupNavigation() {
        launchAndRepeatWithViewLifecycle {
            viewModel.navigationUiState.collect { navigationUiState ->

                if (navigationUiState.isMoveAdd) {
                    val action = viewModel.getActionForMoveAdd()
                    findNavController().navigate(action)
                    viewModel.movedAdd()
                }

                if (navigationUiState.isReturn) {
                    findNavController().popBackStack()
                    viewModel.returned()
                }

            }
        }
    }
}