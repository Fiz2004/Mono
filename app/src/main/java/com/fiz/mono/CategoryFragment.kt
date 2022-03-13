package com.fiz.mono

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fiz.mono.databinding.FragmentCategoryBinding
import com.fiz.mono.ui.input.CategoryInputAdapter
import com.fiz.mono.ui.input.CategoryItem

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    lateinit var expenseAdapter: CategoryInputAdapter
    lateinit var incomeAdapter: CategoryInputAdapter

    var selectedAdapter: Int? = null
    var selectedItem: Int? = null

    val listExpense = listOf(
        CategoryItem("Bank", R.drawable.bank),
        CategoryItem("Food", R.drawable.food),
        CategoryItem("Medican", R.drawable.medican),
        CategoryItem("Gym", R.drawable.gym),
        CategoryItem("Coffee", R.drawable.coffee),
        CategoryItem("Shopping", R.drawable.market),
        CategoryItem("Cats", R.drawable.cat),
        CategoryItem("Party", R.drawable.party),
        CategoryItem("Gift", R.drawable.gift),
        CategoryItem("Gas", R.drawable.gas),
        CategoryItem("Add more", null),
    )

    val listIncome = listOf(
        CategoryItem("Freelance", R.drawable.challenge),
        CategoryItem("Salary", R.drawable.money),
        CategoryItem("Bonus", R.drawable.coin),
        CategoryItem("Loan", R.drawable.user),
        CategoryItem("Add more", null),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setExpenseAdapter()

        setIncomeAdapter()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setIncomeAdapter() {
        incomeAdapter = CategoryInputAdapter { position ->
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
        incomeAdapter.submitList(listIncome)
        binding.incomeRecyclerView.adapter = incomeAdapter
    }

    private fun setExpenseAdapter() {
        expenseAdapter = CategoryInputAdapter { position ->
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
        expenseAdapter.submitList(listExpense)
        binding.expenseRecyclerView.adapter = expenseAdapter
    }
}