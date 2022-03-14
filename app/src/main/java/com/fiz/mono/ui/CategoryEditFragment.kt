package com.fiz.mono.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.databinding.FragmentCategoryEditBinding
import com.fiz.mono.ui.input.CategoryInputAdapter


class CategoryEditFragment : Fragment() {

    private val args: CategoryEditFragmentArgs by navArgs()

    private var _binding: FragmentCategoryEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseAdapter: CategoryInputAdapter
    private lateinit var incomeAdapter: CategoryInputAdapter

    var selectedAdapter: Int? = null
    var selectedItem: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryEditBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = args.type
        val name = args.name.toString()
        val icon = args.icon
        if (type != "") {
            if (type == "expense") {
                CategoryStore.insertNewCategoryExpense(CategoryItem(name, icon))
            } else {
                CategoryStore.insertNewCategoryIncome(CategoryItem(name, icon))
            }
        }

        setExpenseAdapter()

        setIncomeAdapter()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.removeButton.setOnClickListener {
            if (selectedAdapter == 0) {
                if (selectedItem != null) {
                    CategoryStore.removeCategoryExpense(selectedItem!!)
                    expenseAdapter.submitList(CategoryStore.getAllCategoryExpense())
                    expenseAdapter.notifyItemRemoved(selectedItem!!)
                }
            } else {
                if (selectedItem != null) {
                    CategoryStore.removeCategoryIncome(selectedItem!!)
                    incomeAdapter.submitList(CategoryStore.getAllCategoryIncome())
                    incomeAdapter.notifyItemRemoved(selectedItem!!)
                }
            }
        }

    }

    private fun setIncomeAdapter() {
        incomeAdapter = CategoryInputAdapter { position ->

            if (position == CategoryStore.getAllCategoryIncome().size - 1) {
                val action =
                    CategoryEditFragmentDirections
                        .actionCategoryFragmentToCategoryAddFragment("income")
                view?.findNavController()?.navigate(action)
                return@CategoryInputAdapter
            }



            if (selectedAdapter != 1) {
                val old = selectedItem
                selectedItem = null
                expenseAdapter.selectedItem = selectedItem
                old?.let { expenseAdapter.notifyItemChanged(old) }
            }
            selectedAdapter = 1
            selectedItem?.let { incomeAdapter.notifyItemChanged(it) }
            incomeAdapter.notifyItemChanged(position)

            if (selectedItem == position) {
                selectedItem = null
                binding.removeButton.visibility = View.GONE
            } else {
                selectedItem = position
                binding.removeButton.visibility = View.VISIBLE
            }

            incomeAdapter.selectedItem = selectedItem
        }
        incomeAdapter.submitList(CategoryStore.getAllCategoryIncome())
        binding.incomeRecyclerView.adapter = incomeAdapter
    }

    private fun setExpenseAdapter() {
        expenseAdapter = CategoryInputAdapter { position ->

            if (position == CategoryStore.getAllCategoryExpense().size - 1) {
                val action =
                    CategoryEditFragmentDirections
                        .actionCategoryFragmentToCategoryAddFragment("expense")
                view?.findNavController()?.navigate(action)
                return@CategoryInputAdapter
            }



            if (selectedAdapter != 0) {
                val old = selectedItem
                selectedItem = null
                incomeAdapter.selectedItem = selectedItem
                old?.let { incomeAdapter.notifyItemChanged(old) }
            }
            selectedAdapter = 0

            selectedItem?.let { expenseAdapter.notifyItemChanged(it) }
            expenseAdapter.notifyItemChanged(position)
            if (selectedItem == position) {
                selectedItem = null
                binding.removeButton.visibility = View.GONE
            } else {
                selectedItem = position
                binding.removeButton.visibility = View.VISIBLE
            }

            expenseAdapter.selectedItem = selectedItem
        }
        expenseAdapter.submitList(CategoryStore.getAllCategoryExpense())
        binding.expenseRecyclerView.adapter = expenseAdapter
    }
}