package com.fiz.mono.ui.report.monthly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.fiz.mono.App
import com.fiz.mono.R
import com.fiz.mono.data.entity.Transaction
import com.fiz.mono.databinding.FragmentReportMonthlyBinding
import com.fiz.mono.ui.MainPreferencesViewModel
import com.fiz.mono.ui.MainPreferencesViewModelFactory
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.report.ReportFragment
import com.fiz.mono.ui.shared_adapters.TransactionsAdapter
import com.fiz.mono.util.TimeUtils
import com.fiz.mono.util.currentUtils
import com.google.android.material.button.MaterialButtonToggleGroup
import java.util.*

class ReportMonthlyFragment : Fragment() {
    private var _binding: FragmentReportMonthlyBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    private val mainPreferencesViewModel: MainPreferencesViewModel by activityViewModels {
        MainPreferencesViewModelFactory(
            requireActivity().getSharedPreferences(
                getString(R.string.preferences),
                AppCompatActivity.MODE_PRIVATE
            )
        )
    }
    private val viewModel: ReportMonthlyViewModel by viewModels {
        ReportMonhlyViewModelFactory(
            (requireActivity().application as App).transactionStore
        )
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

        adapter = TransactionsAdapter(
            mainPreferencesViewModel.currency.value ?: "$",
            true
        )

        binding.apply {

            dataRangeLayout.leftDateRangeImageButton.setOnClickListener(::leftDateRangeOnClickListener)
            dataRangeLayout.rightDateRangeImageButton.setOnClickListener(::rightDateRangeOnClickListener)
            dataRangeLayout.dateTextView.setOnClickListener(::dateOnClickListener)
            allExpenseIncomeToggleButton.addOnButtonCheckedListener(::allExpenseIncomeOnButtonCheckedListener)
            transactionsRecyclerView.adapter = adapter
        }

        viewModel.allTransactions.observe(viewLifecycleOwner, ::allTransactionsObserve)

        mainViewModel.date.observe(viewLifecycleOwner, ::dateObserve)
    }

    private fun dateObserve(date: Calendar) {
        binding.dataRangeLayout.dateTextView.text = TimeUtils.getDateMonthYearString(
            date,
            resources.getStringArray(R.array.name_month)
        )
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
                    viewModel.tabSelectedReport = 0
            }
            R.id.toggle2 -> {
                if (isChecked)
                    viewModel.tabSelectedReport = 1
            }
            R.id.toggle3 -> {
                if (isChecked)
                    viewModel.tabSelectedReport = 2
            }
        }
        if (isChecked) {
            adapter.submitList(
                viewModel.getTransactions(
                    viewModel.tabSelectedReport,
                    mainViewModel.date.value!!
                )
            )
        }
    }

    private fun dateOnClickListener(view: View) {
        (requireParentFragment().parentFragment as ReportFragment).clickData()
    }

    private fun allTransactionsObserve(allTransactions: List<Transaction>) {
        val currency = mainPreferencesViewModel.currency.value ?: "$"
        binding.valueReportTextView.text =
            currentUtils.getCurrencyFormat(currency, viewModel.getCurrentBalance(), false)

        binding.incomeValueReportTextView.text =
            currentUtils.getCurrencyFormat(
                currency,
                viewModel.getCurrentIncome(mainViewModel.date.value!!),
                false
            )
        binding.expenseValueReportTextView.text =
            currentUtils.getCurrencyFormat(
                currency,
                viewModel.getCurrentExpense(mainViewModel.date.value!!),
                false
            )

        binding.expenseIncomeValueReportTextView.text =
            currentUtils.getCurrencyFormat(
                currency,
                viewModel.getCurrentIncome(mainViewModel.date.value!!) + viewModel.getCurrentExpense(
                    mainViewModel.date.value!!
                ),
                true
            )

        binding.previousBalanceValueReportTextView.text =
            currentUtils.getCurrencyFormat(
                currency,
                viewModel.getPreviousBalanceValue(mainViewModel.date.value!!),
                false
            )

        adapter.submitList(
            viewModel.getTransactions(
                viewModel.tabSelectedReport,
                mainViewModel.date.value!!
            )
        )
    }
}