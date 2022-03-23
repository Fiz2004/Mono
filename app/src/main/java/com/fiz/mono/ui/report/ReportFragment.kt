package com.fiz.mono.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.R
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.data.database.ItemDatabase
import com.fiz.mono.databinding.FragmentReportBinding
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.getCurrencyFormat
import com.fiz.mono.ui.shared_adapters.TransactionsAdapter
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.themeColor
import com.google.android.material.button.MaterialButtonToggleGroup
import java.text.SimpleDateFormat
import java.util.*


class ReportFragment : Fragment() {
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: ReportViewModel by viewModels {
        val database = ItemDatabase.getDatabase()
        ReportViewModelFactory(TransactionStore(database?.transactionItemDao()!!))
    }

    private lateinit var adapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TransactionsAdapter(mainViewModel.currency)

        binding.apply {
            dataRangeLayout.leftDateRangeImageButton.setOnClickListener(::leftDateRangeOnClickListener)
            dataRangeLayout.rightDateRangeImageButton.setOnClickListener(::rightDateRangeOnClickListener)
            dataRangeLayout.dateTextView.setOnClickListener(::dateOnClickListener)
            choiceReportImageButton.setOnClickListener(::choiceReportOnClickListener)
            monthlyTextView.setOnClickListener(::monthlyOnClickListener)
            categoryTextView.setOnClickListener(::categoryOnClickListener)
            allExpenseIncomeToggleButton.addOnButtonCheckedListener(::allExpenseIncomeOnButtonCheckedListener)
            transactionsRecyclerView.adapter = adapter
        }

        viewModel.allTransactions.observe(viewLifecycleOwner, ::allTransactionsObserve)

        mainViewModel.date.observe(viewLifecycleOwner, ::dateObserve)
    }

    private fun dateObserve(date: Calendar) {
        binding.dataRangeLayout.dateTextView.text =
            SimpleDateFormat("LLLL, yyyy", Locale.US).format(date.time)
    }

    private fun categoryOnClickListener(view: View) {
        mainViewModel.categorySelectedReport = 1
        binding.choiceReportConstraintLayout.visibility = View.GONE
    }

    private fun monthlyOnClickListener(view: View) {
        mainViewModel.categorySelectedReport = 0
        binding.choiceReportConstraintLayout.visibility = View.GONE
    }

    private fun choiceReportOnClickListener(view: View) {
        binding.choiceReportConstraintLayout.visibility =
            if (binding.choiceReportConstraintLayout.visibility == View.GONE)
                View.VISIBLE
            else
                View.GONE

        updateMenu()
    }

    private fun rightDateRangeOnClickListener(view: View) {
        mainViewModel.dateMonthPlusOne()
        allTransactionsObserve(listOf())
    }

    private fun leftDateRangeOnClickListener(view: View) {
        mainViewModel.dateMonthMinusOne()
        allTransactionsObserve(listOf())
    }

    private fun allExpenseIncomeOnButtonCheckedListener(
        toggleButton: MaterialButtonToggleGroup,
        checkedId: Int,
        isChecked: Boolean
    ) {
        when (checkedId) {
            R.id.toggle1 -> {
                if (isChecked)
                    mainViewModel.tabSelectedReport = 0
            }
            R.id.toggle2 -> {
                if (isChecked)
                    mainViewModel.tabSelectedReport = 1
            }
            R.id.toggle3 -> {
                if (isChecked)
                    mainViewModel.tabSelectedReport = 2
            }
        }
        if (isChecked) {
            adapter.submitList(
                viewModel.getTransactions(
                    mainViewModel.tabSelectedReport,
                    mainViewModel.date.value!!
                )
            )
        }
    }

    private fun dateOnClickListener(view: View) {
        val action =
            ReportFragmentDirections
                .actionReportFragmentToCalendarFragment()
        findNavController().navigate(action)
    }

    private fun allTransactionsObserve(allTransactions: List<TransactionItem>) {
        binding.valueReportTextView.text =
            getCurrencyFormat(mainViewModel.currency, viewModel.getCurrentBalance(), false)

        binding.incomeValueReportTextView.text =
            getCurrencyFormat(
                mainViewModel.currency,
                viewModel.getCurrentIncome(mainViewModel.date.value!!),
                false
            )
        binding.expenseValueReportTextView.text =
            getCurrencyFormat(
                mainViewModel.currency,
                viewModel.getCurrentExpense(mainViewModel.date.value!!),
                false
            )

        binding.expenseIncomeValueReportTextView.text =
            getCurrencyFormat(
                mainViewModel.currency,
                viewModel.getCurrentIncome(mainViewModel.date.value!!) + viewModel.getCurrentExpense(
                    mainViewModel.date.value!!
                ),
                true
            )

        binding.previousBalanceValueReportTextView.text =
            getCurrencyFormat(
                mainViewModel.currency,
                viewModel.getPreviousBalanceValue(mainViewModel.date.value!!),
                false
            )

        adapter.submitList(
            viewModel.getTransactions(
                mainViewModel.tabSelectedReport,
                mainViewModel.date.value!!
            )
        )
    }

    private fun updateMenu() {
        if (mainViewModel.categorySelectedReport == 0) {
            binding.monthlyTextView.setTextColor(requireContext().getColorCompat(R.color.blue))
            binding.categoryTextView.setTextColor(requireContext().themeColor(androidx.appcompat.R.attr.colorPrimary))
        } else {
            binding.monthlyTextView.setTextColor(requireContext().themeColor(androidx.appcompat.R.attr.colorPrimary))
            binding.categoryTextView.setTextColor(requireContext().getColorCompat(R.color.blue))
        }
    }
}