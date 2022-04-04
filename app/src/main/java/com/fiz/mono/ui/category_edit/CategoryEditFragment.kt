package com.fiz.mono.ui.category_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
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
        subscribe()
    }

    private fun init() {
        expenseAdapter = CategoriesAdapter(
            (requireActivity().application as App).categoryIconStore,
            R.color.red,
            ::adapterExpenseOnClickListener
        )
        incomeAdapter = CategoriesAdapter(
            (requireActivity().application as App).categoryIconStore,
            R.color.red,
            ::adapterIncomeOnClickListener
        )
    }

    private fun bind() {
        binding.apply {
            navigationBarLayout.backButton.setVisible(true)
            navigationBarLayout.actionButton.setVisible(false)
            navigationBarLayout.actionButton.text = getString(R.string.remove)
            navigationBarLayout.actionButton.setTextColor(requireContext().getColorCompat(R.color.red))
            navigationBarLayout.choiceImageButton.setVisible(false)
            navigationBarLayout.titleTextView.text = getString(R.string.category_edit)
            navigationBarLayout.backButton.setOnClickListener(::backButtonOnClickListener)
            navigationBarLayout.actionButton.setOnClickListener(::removeButtonOnClickListener)
            expenseRecyclerView.adapter = expenseAdapter
            incomeRecyclerView.adapter = incomeAdapter
        }
    }

    private fun subscribe() {
        viewModel.allCategoryExpenseForEdit.observe(viewLifecycleOwner) {
            expenseAdapter.submitList(it)
            incomeAdapter.submitList(viewModel.getCopyAllCategoryItemIncomeForEdit())
        }
        viewModel.allCategoryIncomeForEdit.observe(viewLifecycleOwner) {
            expenseAdapter.submitList(viewModel.getCopyAllCategoryItemExpenseForEdit())
            incomeAdapter.submitList(it)
        }
    }

    private fun backButtonOnClickListener(v: View) {
        findNavController().popBackStack()
    }

    private fun removeButtonOnClickListener(v: View) {
        viewModel.removeSelectItem()
        binding.navigationBarLayout.actionButton.setVisible(viewModel.isSelectedForEdit())
        updateAdapters()
    }

    private fun adapterExpenseOnClickListener(position: Int) {
        if (viewModel.isClickAddPositionExpense(position)) {
            viewModel.cleanSelectedForEdit()
            val action =
                CategoryEditFragmentDirections
                    .actionCategoryFragmentToCategoryAddFragment(TYPE_EXPENSE)
            view?.findNavController()?.navigate(action)
            return
        }

        viewModel.addSelectItemExpenseForEdit(position)
        binding.navigationBarLayout.actionButton.setVisible(viewModel.isSelectedForEdit())
        updateAdapters()
    }

    private fun adapterIncomeOnClickListener(position: Int) {
        if (viewModel.isClickAddPositionIncome(position)) {
            viewModel.cleanSelectedForEdit()
            val action =
                CategoryEditFragmentDirections
                    .actionCategoryFragmentToCategoryAddFragment(TYPE_INCOME)
            view?.findNavController()?.navigate(action)
            return
        }

        viewModel.addSelectItemIncomeForEdit(position)
        binding.navigationBarLayout.actionButton.setVisible(viewModel.isSelectedForEdit())
        updateAdapters()
    }

    private fun updateAdapters() {
        incomeAdapter.submitList(viewModel.getCopyAllCategoryItemIncomeForEdit())
        expenseAdapter.submitList(viewModel.getCopyAllCategoryItemExpenseForEdit())
    }

    companion object {
        const val TYPE_EXPENSE = "expense"
        const val TYPE_INCOME = "income"
    }
}