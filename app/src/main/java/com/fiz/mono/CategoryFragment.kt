package com.fiz.mono

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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

    val listExpense = mutableListOf(
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

    val listIncome = mutableListOf(
        CategoryItem("Freelance", R.drawable.challenge),
        CategoryItem("Salary", R.drawable.money),
        CategoryItem("Bonus", R.drawable.coin),
        CategoryItem("Loan", R.drawable.user),
        CategoryItem("Add more", null),
    )

    val args: CategoryFragmentArgs by navArgs()
    var name: String = ""
    var icon: Int = 0
    var type: String = ""

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

        type = args.type
        name = args.name.toString()
        icon = args.icon
        if (type != "") {
            if (type == "expense") {
                listExpense.add(listExpense.size - 1, CategoryItem(name, icon))
            } else {
                listIncome.add(listIncome.size - 1, CategoryItem(name, icon))
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
                    listExpense.removeAt(selectedItem!!)
                    expenseAdapter.submitList(listExpense)
                    expenseAdapter.notifyItemRemoved(selectedItem!!)
                }
            } else {
                if (selectedItem != null) {
                    listIncome.removeAt(selectedItem!!)
                    incomeAdapter.submitList(listIncome)
                    incomeAdapter.notifyItemRemoved(selectedItem!!)
                }
            }
        }

    }

    private fun setIncomeAdapter() {
        incomeAdapter = CategoryInputAdapter { position ->

            if (position == listIncome.size - 1) {
                val action =
                    CategoryFragmentDirections
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
        incomeAdapter.submitList(listIncome)
        binding.incomeRecyclerView.adapter = incomeAdapter
    }

    private fun setExpenseAdapter() {
        expenseAdapter = CategoryInputAdapter { position ->

            if (position == listExpense.size - 1) {
                val action =
                    CategoryFragmentDirections
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
        expenseAdapter.submitList(listExpense)
        binding.expenseRecyclerView.adapter = expenseAdapter
    }
}