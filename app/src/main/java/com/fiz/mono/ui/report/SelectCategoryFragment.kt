package com.fiz.mono.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.fiz.mono.App
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentSelectCategoryBinding
import com.fiz.mono.ui.shared_adapters.CategoriesAdapter

class SelectCategoryFragment : Fragment() {
    private var _binding: FragmentSelectCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SelectCategoryViewModel by viewModels {
        SelectCategoryViewModelFactory(
            (requireActivity().application as App).categoryStore
        )
    }

    private lateinit var expenseAdapter: CategoriesAdapter
    private lateinit var incomeAdapter: CategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectCategoryBinding.inflate(inflater, container, false)
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
        subscribe()
    }

    private fun init() {
        expenseAdapter = CategoriesAdapter(R.color.red, ::adapterExpenseOnClickListener)
        incomeAdapter = CategoriesAdapter(R.color.red, ::adapterIncomeOnClickListener)
    }

    private fun bind() {
        binding.apply {
            expenseRecyclerView.adapter = expenseAdapter
            incomeRecyclerView.adapter = incomeAdapter
        }
    }

    private fun subscribe() {
        viewModel.allCategoryExpense.observe(viewLifecycleOwner) {
            expenseAdapter.submitList(it)
            incomeAdapter.submitList(viewModel.allCategoryIncome.value)
        }
        viewModel.allCategoryIncome.observe(viewLifecycleOwner) {
            expenseAdapter.submitList(viewModel.allCategoryExpense.value)
            incomeAdapter.submitList(it)
        }
    }

    private fun adapterExpenseOnClickListener(position: Int) {
            val action =
                SelectCategoryFragmentDirections
                    .actionSelectCategoryFragmentToReportCategoryFragment(
                        viewModel.allCategoryExpense.value?.get(
                            position
                        )?.id ?: "", TYPE_EXPENSE
                    )
            view?.findNavController()?.navigate(action)
            return
    }

    private fun adapterIncomeOnClickListener(position: Int) {
            val action =
                SelectCategoryFragmentDirections
                    .actionSelectCategoryFragmentToReportCategoryFragment(
                        viewModel.allCategoryIncome.value?.get(
                            position
                        )?.id ?: "", TYPE_INCOME
                    )
            view?.findNavController()?.navigate(action)
            return
    }

    companion object {
        const val TYPE_EXPENSE = "expense"
        const val TYPE_INCOME = "income"
    }

}


