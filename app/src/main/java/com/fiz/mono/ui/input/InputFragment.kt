package com.fiz.mono.ui.input

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.fiz.mono.R
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.databinding.FragmentInputBinding
import com.fiz.mono.ui.pin_password.PINPasswordFragment
import com.google.android.material.tabs.TabLayout

class InputFragment : Fragment() {
    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    val viewModel: InputViewModel by activityViewModels()

    lateinit var adapter: CategoryInputAdapter

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
        CategoryItem("Edit", null),
    )

    val listIncome = listOf(
        CategoryItem("Freelance", R.drawable.challenge),
        CategoryItem("Salary", R.drawable.money),
        CategoryItem("Bonus", R.drawable.coin),
        CategoryItem("Loan", R.drawable.user),
        CategoryItem("Edit", null),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.firstTime) {
            val action =
                InputFragmentDirections
                    .actionInputFragmentToOnBoardingFragment()
            view.findNavController().navigate(action)
            return
        }
        if (!viewModel.log) {
            val action =
                InputFragmentDirections
                    .actionInputFragmentToPINPasswordFragment(PINPasswordFragment.START)
            view.findNavController().navigate(action)
            return
        }

        binding.currencyTextView.text = viewModel.currency

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    when (tab.text) {
                        getString(R.string.expense) -> {
                            adapter.submitList(listExpense)
                        }
                        getString(R.string.income) -> {
                            adapter.submitList(listIncome)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.noteCameraInputEditText.setOnClickListener {
            // Respond to end icon presses
        }

        adapter = CategoryInputAdapter { position ->

        }
        adapter.submitList(listExpense)
        binding.categoryInputRecyclerView.adapter = adapter

    }

    private fun isLogOut(): Boolean {
        return true
    }

    private fun isFirstTime(): Boolean {
        return true
    }
}

