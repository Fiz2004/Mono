package com.fiz.mono.ui.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.R
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.data.database.ItemDatabase
import com.fiz.mono.databinding.FragmentReportMonthlyBinding
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.shared_adapters.TransactionsAdapter
import com.fiz.mono.util.currentUtils
import com.google.android.material.button.MaterialButtonToggleGroup
import java.text.SimpleDateFormat
import java.util.*

class ReportMonthlyFragment : Fragment() {
    private var _binding: FragmentReportMonthlyBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val monthlyViewModel: ReportMonthlyViewModel by viewModels {
        val database = ItemDatabase.getDatabase()
        ReportMonhlyViewModelFactory(TransactionStore(database?.transactionItemDao()!!))
    }

    private lateinit var adapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportMonthlyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = mainViewModel.currency.value?.let {
            TransactionsAdapter(it)
        } ?: TransactionsAdapter("$")

        binding.apply {

            dataRangeLayout.leftDateRangeImageButton.setOnClickListener(::leftDateRangeOnClickListener)
            dataRangeLayout.rightDateRangeImageButton.setOnClickListener(::rightDateRangeOnClickListener)
            dataRangeLayout.dateTextView.setOnClickListener(::dateOnClickListener)
            allExpenseIncomeToggleButton.addOnButtonCheckedListener(::allExpenseIncomeOnButtonCheckedListener)
            transactionsRecyclerView.adapter = adapter
        }

        monthlyViewModel.allTransactions.observe(viewLifecycleOwner, ::allTransactionsObserve)

        mainViewModel.date.observe(viewLifecycleOwner, ::dateObserve)
    }

    private fun dateObserve(date: Calendar) {
        binding.dataRangeLayout.dateTextView.text =
            SimpleDateFormat("LLLL, yyyy", Locale.getDefault()).format(date.time)
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
                    monthlyViewModel.tabSelectedReport = 0
            }
            R.id.toggle2 -> {
                if (isChecked)
                    monthlyViewModel.tabSelectedReport = 1
            }
            R.id.toggle3 -> {
                if (isChecked)
                    monthlyViewModel.tabSelectedReport = 2
            }
        }
        if (isChecked) {
            adapter.submitList(
                monthlyViewModel.getTransactions(
                    monthlyViewModel.tabSelectedReport,
                    mainViewModel.date.value!!
                )
            )
        }
    }

    private fun dateOnClickListener(view: View) {
        val action =
            ReportFragmentDirections
                .actionToCalendarFragment()
        findNavController().navigate(action)
    }

    private fun allTransactionsObserve(allTransactions: List<TransactionItem>) {
        val currency = mainViewModel.currency.value ?: "$"
        binding.valueReportTextView.text =
            currentUtils.getCurrencyFormat(currency, monthlyViewModel.getCurrentBalance(), false)

        binding.incomeValueReportTextView.text =
            currentUtils.getCurrencyFormat(
                currency,
                monthlyViewModel.getCurrentIncome(mainViewModel.date.value!!),
                false
            )
        binding.expenseValueReportTextView.text =
            currentUtils.getCurrencyFormat(
                currency,
                monthlyViewModel.getCurrentExpense(mainViewModel.date.value!!),
                false
            )

        binding.expenseIncomeValueReportTextView.text =
            currentUtils.getCurrencyFormat(
                currency,
                monthlyViewModel.getCurrentIncome(mainViewModel.date.value!!) + monthlyViewModel.getCurrentExpense(
                    mainViewModel.date.value!!
                ),
                true
            )

        binding.previousBalanceValueReportTextView.text =
            currentUtils.getCurrencyFormat(
                currency,
                monthlyViewModel.getPreviousBalanceValue(mainViewModel.date.value!!),
                false
            )

        adapter.submitList(
            monthlyViewModel.getTransactions(
                monthlyViewModel.tabSelectedReport,
                mainViewModel.date.value!!
            )
        )
    }
}