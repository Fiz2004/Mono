package com.fiz.mono.feature_report.ui.monthly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.ui.MainViewModel
import com.fiz.mono.core.ui.shared_adapters.TransactionsAdapter
import com.fiz.mono.core.util.TimeUtils
import com.fiz.mono.core.util.launchAndRepeatWithViewLifecycle
import com.fiz.mono.databinding.FragmentReportMonthlyBinding
import com.fiz.mono.feature_report.ui.ReportFragment
import com.fiz.mono.feature_report.ui.ReportViewModel
import com.google.android.material.button.MaterialButtonToggleGroup
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate

@AndroidEntryPoint
class ReportMonthlyFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()

    private val mainPreferencesViewModel: com.fiz.mono.core.ui.MainPreferencesViewModel by activityViewModels()

    private val viewModel: ReportMonthlyViewModel by viewModels()

    private val parentViewModel: ReportViewModel by viewModels()

    private val adapter: TransactionsAdapter by lazy {
        TransactionsAdapter(
            mainPreferencesViewModel.currency.value ?: "$",
            true
        )
    }

    private val toggleButtons =
        listOf(com.fiz.mono.R.id.toggle1, com.fiz.mono.R.id.toggle2, com.fiz.mono.R.id.toggle3)

    private lateinit var binding: FragmentReportMonthlyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportMonthlyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        bind()
        bindListener()
        subscribe()
    }

    private fun init() {
        viewModel.start(mainPreferencesViewModel.currency.value ?: "$")
    }

    private fun bind() {
        binding.apply {
            transactionsRecyclerView.adapter = adapter
        }
    }

    private fun bindListener() {
        binding.apply {
            dataRangeLayout.leftDateRangeImageButton.setOnClickListener {
                mainViewModel.dateMonthMinusOne()
            }

            dataRangeLayout.rightDateRangeImageButton.setOnClickListener {
                mainViewModel.dateMonthPlusOne()
            }

            dataRangeLayout.dateTextView.setOnClickListener {
                (requireParentFragment().parentFragment as ReportFragment).clickData()
            }

            allExpenseIncomeToggleButton.addOnButtonCheckedListener { toggleButton: MaterialButtonToggleGroup,
                                                                      checkedId: Int,
                                                                      isChecked: Boolean ->
                if (isChecked)
                    viewModel.onEvent(
                        ReportMonthlyUiEvent.ClickTransactionsFilter(toggleButtons.indexOf(checkedId))
                    )
            }
        }
    }

    private fun subscribe() {
        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collect { uiState ->
                binding.valueReportTextView.text = uiState.currentBalance
                binding.incomeValueReportTextView.text = uiState.currentIncome
                binding.expenseValueReportTextView.text = uiState.currentExpense
                binding.expenseIncomeValueReportTextView.text = uiState.currentExpenseIncome
                binding.previousBalanceValueReportTextView.text =
                    uiState.lastBalance

                adapter.submitList(uiState.transactionsForAdapter)
                if (uiState.isDateChange)
                    viewModel.dataChanged()
            }
        }

        mainViewModel.date.observe(viewLifecycleOwner, ::dateObserve)
    }

    private fun dateObserve(date: LocalDate) {
        binding.dataRangeLayout.dateTextView.text = TimeUtils.getDateMonthYearString(
            date,
            resources.getStringArray(R.array.name_month)
        )
        viewModel.onEvent(
            ReportMonthlyUiEvent.ObserveData(date)
        )
    }
}