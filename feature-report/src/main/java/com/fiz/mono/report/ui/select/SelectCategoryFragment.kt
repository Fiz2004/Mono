package com.fiz.mono.report.ui.select

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.base.android.adapters.CategoriesAdapter
import com.fiz.mono.feature_report.ui.select.SelectCategoryFragmentDirections
import com.fiz.mono.report.databinding.FragmentSelectCategoryBinding
import com.fiz.mono.util.launchAndRepeatWithViewLifecycle
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

        bind()
        subscribe()

        setupNavigation()
    }

    private fun bind() {
        binding.apply {
            expenseRecyclerView.adapter = expenseAdapter
            incomeRecyclerView.adapter = incomeAdapter
        }
    }

    private fun subscribe() {
        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collect { uiState ->
                expenseAdapter.submitList(uiState.allCategoryExpense)
                incomeAdapter.submitList(uiState.allCategoryIncome)
            }
        }
    }


    private fun setupNavigation() {
        launchAndRepeatWithViewLifecycle {
            viewModel.navigationUiState.collect { navigationUiState ->
                if (navigationUiState.isMove) {
                    val action =
                        SelectCategoryFragmentDirections
                            .actionSelectCategoryFragmentToReportCategoryFragment(
                                navigationUiState.id,
                                navigationUiState.type
                            )
                    findNavController().navigate(action)
                    viewModel.moved()
                }
            }
        }
    }

    companion object {
        const val TYPE_EXPENSE = "expense"
        const val TYPE_INCOME = "income"
    }

}


