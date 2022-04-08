package com.fiz.mono.ui.report.monthly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fiz.mono.App
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentReportMonthlyBinding
import com.fiz.mono.ui.MainPreferencesViewModel
import com.fiz.mono.ui.MainPreferencesViewModelFactory
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.report.ReportFragment
import com.fiz.mono.ui.shared_adapters.TransactionsAdapter
import com.fiz.mono.util.TimeUtils
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.coroutines.launch
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

        init()
        bind()
        bindListener()
        subscribe()

    }

    private fun init() {
        adapter = TransactionsAdapter(
            mainPreferencesViewModel.currency.value ?: "$",
            true
        )

        viewModel.init(mainPreferencesViewModel.currency.value ?: "$")
    }

    private fun bind() {
        binding.apply {
            transactionsRecyclerView.adapter = adapter
        }
    }

    private fun bindListener() {
        binding.apply {

            dataRangeLayout.leftDateRangeImageButton.setOnClickListener(::leftDateRangeOnClickListener)
            dataRangeLayout.rightDateRangeImageButton.setOnClickListener(::rightDateRangeOnClickListener)
            dataRangeLayout.dateTextView.setOnClickListener(::dateOnClickListener)
            allExpenseIncomeToggleButton.addOnButtonCheckedListener(::allExpenseIncomeOnButtonCheckedListener)
        }
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    binding.valueReportTextView.text = uiState.currentBalance
                    binding.incomeValueReportTextView.text = uiState.currentIncome
                    binding.expenseValueReportTextView.text = uiState.currentExpense
                    binding.expenseIncomeValueReportTextView.text = uiState.currentExpenseIncome
                    binding.previousBalanceValueReportTextView.text =
                        uiState.currentPreviousBalance

                    adapter.submitList(uiState.transactionsForAdapter)
                    if (uiState.isDateChange)
                        viewModel.onDataChange()
                }
            }
        }

        mainViewModel.date.observe(viewLifecycleOwner, ::dateObserve)
    }

    private fun dateObserve(date: Calendar) {
        binding.dataRangeLayout.dateTextView.text = TimeUtils.getDateMonthYearString(
            date,
            resources.getStringArray(R.array.name_month)
        )
        viewModel.setDate(date)
    }

    private fun rightDateRangeOnClickListener(view: View) {
        mainViewModel.dateMonthPlusOne()
    }

    private fun leftDateRangeOnClickListener(view: View) {
        mainViewModel.dateMonthMinusOne()
    }

    private fun allExpenseIncomeOnButtonCheckedListener(
        toggleButton: MaterialButtonToggleGroup,
        checkedId: Int,
        isChecked: Boolean
    ) {
        when (checkedId) {
            R.id.toggle1 -> {
                if (isChecked)
                    viewModel.setTabSelectedReport(0)
            }
            R.id.toggle2 -> {
                if (isChecked)
                    viewModel.setTabSelectedReport(1)
            }
            R.id.toggle3 -> {
                if (isChecked)
                    viewModel.setTabSelectedReport(2)
            }
        }
    }

    private fun dateOnClickListener(view: View) {
        (requireParentFragment().parentFragment as ReportFragment).clickData()
    }
}