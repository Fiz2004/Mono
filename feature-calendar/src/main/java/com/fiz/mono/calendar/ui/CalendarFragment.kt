package com.fiz.mono.calendar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.base.android.adapters.TransactionsAdapter
import com.fiz.mono.base.android.utils.launchAndRepeatWithViewLifecycle
import com.fiz.mono.base.android.utils.setVisible
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.feature.calendar.databinding.FragmentCalendarBinding
import com.fiz.mono.navigation.navigate
import com.fiz.mono.util.TimeUtils.getDateMonthYearString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalendarFragment : Fragment(), MonthDialog.Choicer {

    private val viewModel: CalendarViewModel by viewModels()

    private val calendarAdapter: CalendarAdapter by lazy {
        CalendarAdapter { transactionsDay ->
            viewModel.setDate(transactionsDay.day)
        }
    }

    private val transactionAdapter: TransactionsAdapter by lazy {
        TransactionsAdapter(
            viewModel.viewState.value.currency,
            true
        ) { transactionItem ->
            navigate(
                com.fiz.mono.feature.calendar.R.id.action_calendarFragment_to_inputFragment,
                data = transactionItem.id,
            )
        }
    }

    private lateinit var binding: FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupListeners()
        observeViewStateUpdates()
        observeViewEffects()
    }

    private fun setupUI() {
        binding.apply {
            calendarRecyclerView.adapter = calendarAdapter
            transactionRecyclerView.adapter = transactionAdapter
        }
    }

    private fun setupListeners() {
        binding.apply {
            navigationBarLayout.setOnClickListenerBackButton {
                viewModel.clickBackButton()
            }

            navigationBarLayout.setOnClickListenerChoiceButton {
                val monthDialog = MonthDialog()
                monthDialog.choicer = this@CalendarFragment

                val args = Bundle()
                val currentMonth = viewModel.viewState.value.date.monthValue
                args.putInt("currentMonth", currentMonth)
                monthDialog.arguments = args

                monthDialog.show(childFragmentManager, "Choice Month")
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

    private fun updateScreenState(newState: CalendarViewState) {
        if (newState.isAllTransactionsLoaded) {
            binding.calendarRecyclerView.itemAnimator = null
            calendarAdapter.submitList(newState.calendarDataItem)
            transactionAdapter.submitList(newState.transactionsDataItem)
            binding.noTransactionsTextView.setVisible(newState.transactionsDataItem.isEmpty())
            binding.transactionRecyclerView.setVisible(newState.transactionsDataItem.isNotEmpty())
            if (newState.isAllTransactionsLoaded)
                viewModel.onAllTransactionsLoaded()
        }

        binding.navigationBarLayout.setTextTitle(
            getDateMonthYearString(
                newState.date,
                resources.getStringArray(R.array.name_month)
            )
        )
        viewModel.changeData(newState.date)
    }


    override fun choiceMonth(numberMonth: Int) {
        viewModel.setMonth(numberMonth)
    }

    private fun observeViewEffects() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewEffects.collect { viewEffect ->
                when (viewEffect) {
                    CalendarViewEffect.MoveReturn -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }
}