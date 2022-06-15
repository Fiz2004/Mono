package com.fiz.mono.report.ui.monthly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fiz.mono.base.android.adapters.TransactionsAdapter
import com.fiz.mono.base.android.utils.launchAndRepeatWithViewLifecycle
import com.fiz.mono.feature.report.R
import com.fiz.mono.feature.report.databinding.FragmentReportMonthlyBinding
import com.fiz.mono.report.ui.ReportEvent
import com.fiz.mono.report.ui.ReportViewModel
import com.fiz.mono.util.TimeUtils
import com.google.android.material.button.MaterialButtonToggleGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportMonthlyFragment : Fragment() {

    private val viewModel: ReportMonthlyViewModel by viewModels()

    private val viewModelParent: ReportViewModel by viewModels(
        ownerProducer = { requireParentFragment().requireParentFragment() }
    )

    private val adapter: TransactionsAdapter by lazy {
        TransactionsAdapter(
            viewModel.viewState.value.currency,
            true
        )
    }

    private val toggleButtons =
        listOf(
            R.id.toggle1,
            R.id.toggle2,
            R.id.toggle3
        )

    private val binding get() = _binding!!
    private var _binding: FragmentReportMonthlyBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportMonthlyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupListeners()
        observeViewStateUpdates()
    }

    private fun setupUI() {
        binding.apply {
            transactionsRecyclerView.adapter = adapter
        }
    }

    private fun setupListeners() {
        binding.apply {
            dataRangeLayout.leftDateRangeImageButton.setOnClickListener {
                viewModel.onEvent(ReportMonthlyEvent.DateLeftClicked)
            }

            dataRangeLayout.rightDateRangeImageButton.setOnClickListener {
                viewModel.onEvent(ReportMonthlyEvent.DateRightClicked)
            }

            dataRangeLayout.dateTextView.setOnClickListener {
                viewModelParent.onEvent(ReportEvent.DateTextClicked)
            }

            allExpenseIncomeToggleButton.addOnButtonCheckedListener { toggleButton: MaterialButtonToggleGroup,
                                                                      checkedId: Int,
                                                                      isChecked: Boolean ->
                if (isChecked) {
                    val numberButton = toggleButtons.indexOf(checkedId)
                    viewModel.onEvent(
                        ReportMonthlyEvent.TransactionsFilterClicked(numberButton)
                    )
                }
            }
        }
    }

    private fun observeViewStateUpdates() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collect { newState ->
                updateScreenState(newState)
            }
        }
    }

    private fun updateScreenState(newState: ReportMonthlyViewState) {
        binding.valueReportTextView.text = newState.currentBalance
        binding.incomeValueReportTextView.text = newState.currentIncome
        binding.expenseValueReportTextView.text = newState.currentExpense
        binding.expenseIncomeValueReportTextView.text = newState.currentExpenseIncome
        binding.previousBalanceValueReportTextView.text = newState.lastBalance

        adapter.submitList(newState.transactionsForMonth)

        binding.dataRangeLayout.dateTextView.text = TimeUtils.getDateMonthYearString(
            newState.date,
            resources.getStringArray(com.fiz.mono.common.ui.resources.R.array.name_month)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.transactionsRecyclerView.adapter = null
        _binding = null
    }
}