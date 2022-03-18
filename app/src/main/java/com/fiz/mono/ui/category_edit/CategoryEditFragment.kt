package com.fiz.mono.ui.category_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentCategoryEditBinding
import com.fiz.mono.util.CategoryInputAdapter


class CategoryEditFragment : Fragment() {
    private var _binding: FragmentCategoryEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryEditViewModel by viewModels()

    private lateinit var expenseAdapter: CategoryInputAdapter
    private lateinit var incomeAdapter: CategoryInputAdapter

    private val args: CategoryEditFragmentArgs by navArgs()

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

        if (args.type != "") {
            viewModel.insertNewCategory(args.type, args.name.toString(), args.icon)
        }
        binding.backButton.setOnClickListener(::backButtonOnClickListener)
        binding.removeButton.setOnClickListener(::removeButtonOnClickListener)

        expenseAdapter = CategoryInputAdapter(R.color.red, ::adapterExpenseOnClickListener)
        expenseAdapter.submitList(viewModel.getAllCategoryItemExpense())
        binding.expenseRecyclerView.adapter = expenseAdapter

        incomeAdapter = CategoryInputAdapter(R.color.red, ::adapterIncomeOnClickListener)
        incomeAdapter.submitList(viewModel.getAllCategoryItemIncome())
        binding.incomeRecyclerView.adapter = incomeAdapter
    }

    private fun backButtonOnClickListener(v: View): Unit {
        findNavController().popBackStack()
    }

    private fun removeButtonOnClickListener(v: View): Unit {
        viewModel.removeSelectItem()
        binding.removeButton.visibility = viewModel.getVisibilityRemoveButton()
        updateAdapters()
    }

    private fun adapterExpenseOnClickListener(position: Int) {
        viewModel.addSelectItemExpense(position)
        binding.removeButton.visibility = viewModel.getVisibilityRemoveButton()
        updateAdapters()

        if (viewModel.isClickAddPositionExpense(position)) {
            viewModel.cleanSelected()
            val action =
                CategoryEditFragmentDirections
                    .actionCategoryFragmentToCategoryAddFragment(TYPE_EXPENSE)
            view?.findNavController()?.navigate(action)
        }

    }

    private fun adapterIncomeOnClickListener(position: Int) {
        viewModel.addSelectItemIncome(position)
        binding.removeButton.visibility = viewModel.getVisibilityRemoveButton()
        updateAdapters()

        if (viewModel.isClickAddPositionIncome(position)) {
            viewModel.cleanSelected()
            val action =
                CategoryEditFragmentDirections
                    .actionCategoryFragmentToCategoryAddFragment(TYPE_INCOME)
            view?.findNavController()?.navigate(action)
        }
    }

    private fun updateAdapters() {
        incomeAdapter.submitList(viewModel.getAllCategoryItemIncome())
        expenseAdapter.submitList(viewModel.getAllCategoryItemExpense())
    }

    companion object {
        const val TYPE_EXPENSE = "expense"
        const val TYPE_INCOME = "income"
    }
}