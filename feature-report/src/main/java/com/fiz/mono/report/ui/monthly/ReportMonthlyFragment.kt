package com.fiz.mono.report.ui.monthly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fiz.mono.base.android.adapters.TransactionsAdapter
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.util.TimeUtils
import com.fiz.mono.report.databinding.FragmentReportMonthlyBinding
import com.fiz.mono.report.ui.ReportFragment
import com.fiz.mono.util.launchAndRepeatWithViewLifecycle
import com.google.android.material.button.MaterialButtonToggleGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportMonthlyFragment : Fragment() {

    private val viewModel: ReportMonthlyViewModel by viewModels()

    private val adapter: TransactionsAdapter by lazy {
        TransactionsAdapter(
            viewModel.uiState.value.currency,
            true
        )
    }

    private val toggleButtons =
        listOf(com.fiz.mono.report.R.id.toggle1, com.fiz.mono.report.R.id.toggle2, com.fiz.mono.report.R.id.toggle3)

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

        bind()
        bindListener()
        subscribe()
    }

    private fun bind() {
        binding.apply {
            transactionsRecyclerView.adapter = adapter
        }
    }

    private fun bindListener() {
        binding.apply {
            dataRangeLayout.leftDateRangeImageButton.setOnClickListener {
                viewModel.onEvent(ReportMonthlyUiEvent.ClickDateLeft)

            }

            dataRangeLayout.rightDateRangeImageButton.setOnClickListener {
                viewModel.onEvent(ReportMonthlyUiEvent.ClickDateRight)
            }

            dataRangeLayout.dateTextView.setOnClickListener {
                (requireParentFragment().parentFragment as ReportFragment).clickData()
            }

            allExpenseIncomeToggleButton.addOnButtonCheckedListener { toggleButton: MaterialButtonToggleGroup,
                                                                      checkedId: Int,
                                                                      isChecked: Boolean ->
                if (isChecked) {
                    val numberButton = toggleButtons.indexOf(checkedId)
                    viewModel.onEvent(
                        ReportMonthlyUiEvent.ClickTransactionsFilter(numberButton)
                    )
                }
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
                binding.previousBalanceValueReportTextView.text = uiState.lastBalance

                adapter.submitList(uiState.transactionsForMonth)

                binding.dataRangeLayout.dateTextView.text = TimeUtils.getDateMonthYearString(
                    uiState.date,
                    resources.getStringArray(R.array.name_month)
                )
            }
        }
    }
}