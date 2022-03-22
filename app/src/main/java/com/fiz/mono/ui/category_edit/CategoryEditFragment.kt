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
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.database.ItemDatabase
import com.fiz.mono.databinding.FragmentCategoryEditBinding
import com.fiz.mono.util.CategoriesAdapter


class CategoryEditFragment : Fragment() {
    private var _binding: FragmentCategoryEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryEditViewModel by viewModels {
        CategoryEditViewModelFactory(
            CategoryStore(
                ItemDatabase.getDatabase()?.categoryItemDao()!!
            )
        )
    }

    private lateinit var expenseAdapter: CategoriesAdapter
    private lateinit var incomeAdapter: CategoriesAdapter

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

        viewModel.insertNewCategory(args.type, args.name.toString(), args.icon)

        binding.backButton.setOnClickListener(::backButtonOnClickListener)
        binding.removeButton.setOnClickListener(::removeButtonOnClickListener)

        expenseAdapter = CategoriesAdapter(R.color.red, ::adapterExpenseOnClickListener)
        binding.expenseRecyclerView.adapter = expenseAdapter

        incomeAdapter = CategoriesAdapter(R.color.red, ::adapterIncomeOnClickListener)
        binding.incomeRecyclerView.adapter = incomeAdapter

        viewModel.allCategoryExpense.observe(viewLifecycleOwner) {
            expenseAdapter.submitList(it)
            incomeAdapter.submitList(viewModel.getAllCategoryItemIncome())
        }
        viewModel.allCategoryIncome.observe(viewLifecycleOwner) {
            expenseAdapter.submitList(viewModel.getAllCategoryItemExpense())
            incomeAdapter.submitList(it)
        }
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
        if (viewModel.isClickAddPositionExpense(position)) {
            viewModel.cleanSelected()
            val action =
                CategoryEditFragmentDirections
                    .actionCategoryFragmentToCategoryAddFragment(TYPE_EXPENSE)
            view?.findNavController()?.navigate(action)
            return
        }

        viewModel.addSelectItemExpense(position)
        binding.removeButton.visibility = viewModel.getVisibilityRemoveButton()
        updateAdapters()
    }

    private fun adapterIncomeOnClickListener(position: Int) {
        if (viewModel.isClickAddPositionIncome(position)) {
            viewModel.cleanSelected()
            val action =
                CategoryEditFragmentDirections
                    .actionCategoryFragmentToCategoryAddFragment(TYPE_INCOME)
            view?.findNavController()?.navigate(action)
            return
        }

        viewModel.addSelectItemIncome(position)
        binding.removeButton.visibility = viewModel.getVisibilityRemoveButton()
        updateAdapters()
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