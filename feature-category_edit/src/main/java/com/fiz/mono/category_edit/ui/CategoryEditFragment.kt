package com.fiz.mono.category_edit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.base.android.adapters.CategoriesAdapter
import com.fiz.mono.base.android.utils.launchAndRepeatWithViewLifecycle
import com.fiz.mono.category_edit.databinding.FragmentCategoryEditBinding
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.navigation.navigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryEditFragment : Fragment() {

    private val viewModel: CategoryEditViewModel by viewModels()

    private val expenseAdapter: CategoriesAdapter by lazy {
        CategoriesAdapter(
            R.color.red
        ) { position ->
            viewModel.onEvent(CategoryEditEvent.ExpenseItemClicked(position))
        }
    }

    private val incomeAdapter: CategoriesAdapter by lazy {
        CategoriesAdapter(
            R.color.red
        ) { position ->
            viewModel.onEvent(CategoryEditEvent.IncomeItemClicked(position))
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

        setupUI()
        setupListeners()
        observeViewStateUpdates()

        observeViewEffects()
    }

    private fun setupUI() {
        binding.apply {
            expenseRecyclerView.adapter = expenseAdapter
            incomeRecyclerView.adapter = incomeAdapter
        }
    }

    private fun setupListeners() {
        binding.apply {
            navigationBarLayout.setOnClickListenerBackButton {
                viewModel.onEvent(CategoryEditEvent.BackButtonClicked)
            }

            navigationBarLayout.setOnClickListenerActionButton {
                viewModel.onEvent(CategoryEditEvent.RemoveButtonClicked)
            }
        }
        Button(context).setOnClickListener { }
    }

    private fun observeViewStateUpdates() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collect { newState ->
                updateScreenState(newState)
            }
        }
    }

    private fun updateScreenState(newState: CategoryEditViewState) {
        binding.navigationBarLayout.setVisibilityActionButton(newState.isRemoveButtonVisible)

        expenseAdapter.submitList(newState.allCategoryExpense)
        incomeAdapter.submitList(newState.allCategoryIncome)
    }

    private fun observeViewEffects() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewEffects.collect { viewEffect ->

                when (viewEffect) {
                    CategoryEditViewEffect.MoveCategoryAdd -> {
                        navigate(
                            com.fiz.mono.category_edit.R.id.action_categoryFragment_to_categoryAddFragment,
                            data = viewModel.getType()
                        )
                    }
                    CategoryEditViewEffect.MoveReturn -> {
                        findNavController().popBackStack()
                    }
                }

            }
        }
    }
}