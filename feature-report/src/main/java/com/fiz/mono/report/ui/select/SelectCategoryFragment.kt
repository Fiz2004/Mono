package com.fiz.mono.report.ui.select

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fiz.mono.base.android.adapters.CategoriesAdapter
import com.fiz.mono.base.android.utils.launchAndRepeatWithViewLifecycle
import com.fiz.mono.feature.report.R
import com.fiz.mono.feature.report.databinding.FragmentSelectCategoryBinding
import com.fiz.mono.navigation.CategoryInfoArgs
import com.fiz.mono.navigation.navigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectCategoryFragment : Fragment() {

    private val viewModel: SelectCategoryViewModel by viewModels()

    private val expenseAdapter: CategoriesAdapter by lazy {
        CategoriesAdapter(com.fiz.mono.common.ui.resources.R.color.red) { position ->
            viewModel.clickExpenseRecyclerView(position)
        }
    }

    private val incomeAdapter: CategoriesAdapter by lazy {
        CategoriesAdapter(com.fiz.mono.common.ui.resources.R.color.red) { position ->
            viewModel.clickIncomeRecyclerView(position)
        }
    }
    private lateinit var binding: FragmentSelectCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewStateUpdates()
        observeViewEffects()
    }

    private fun setupUI() {
        binding.apply {
            expenseRecyclerView.adapter = expenseAdapter
            incomeRecyclerView.adapter = incomeAdapter
        }
    }

    private fun observeViewStateUpdates() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collect { newState ->
                updateScreenState(newState)
            }
        }
    }

    private fun updateScreenState(newState: SelectViewState) {
        expenseAdapter.submitList(newState.allCategoryExpense)
        incomeAdapter.submitList(newState.allCategoryIncome)
    }

    private fun observeViewEffects() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewEffects.collect { viewEffect ->
                reactTo(viewEffect)
            }
        }
    }

    private fun reactTo(viewEffect: SelectViewEffect) {
        when (viewEffect) {
            is SelectViewEffect.MoveCategoryReport -> {
                val categoryInfoArgs =
                    CategoryInfoArgs(viewEffect.type, viewEffect.id)
                navigate(
                    R.id.action_selectCategoryFragment_to_reportCategoryFragment,
                    com.fiz.mono.navigation.R.id.report_host_fragment,
                    categoryInfoArgs
                )
            }
        }
    }

}


