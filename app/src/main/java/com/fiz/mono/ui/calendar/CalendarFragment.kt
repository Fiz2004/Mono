package com.fiz.mono.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentCalendarBinding
import com.fiz.mono.ui.MainPreferencesViewModel
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.shared_adapters.TransactionsAdapter
import com.fiz.mono.util.TimeUtils.getDateMonthYearString
import com.fiz.mono.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CalendarFragment : Fragment(), MonthDialog.Choicer {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CalendarViewModel by viewModels()

    private val mainViewModel: MainViewModel by activityViewModels()

    private val mainPreferencesViewModel: MainPreferencesViewModel by activityViewModels()

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var transactionAdapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
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

    internal fun init() {
        calendarAdapter = CalendarAdapter { transactionsDay ->
            mainViewModel.setDate(transactionsDay.day)
        }

        val currency = mainPreferencesViewModel.currency.value ?: "$"
        transactionAdapter =
            TransactionsAdapter(
                currency,
                true
            ) { transactionItem ->
                val action =
                    CalendarFragmentDirections
                        .actionCalendarFragmentToInputFragment(transaction = transactionItem.id)
                findNavController().navigate(action)
            }
    }

    private fun bind() {
        binding.apply {
            navigationBarLayout.backButton.setVisible(true)
            navigationBarLayout.actionButton.setVisible(false)
            navigationBarLayout.choiceImageButton.setVisible(true)
            calendarRecyclerView.adapter = calendarAdapter
            transactionRecyclerView.adapter = transactionAdapter
        }
    }

    private fun bindListener() {
        binding.apply {
            navigationBarLayout.backButton.setOnClickListener {
                viewModel.clickBackButton()
            }

            navigationBarLayout.choiceImageButton.setOnClickListener {
                val monthDialog = MonthDialog()
                monthDialog.choicer = this@CalendarFragment

                val args = Bundle()
                val currentMonth = mainViewModel.date.value?.monthValue ?: 1
                args.putInt("currentMonth", currentMonth)
                monthDialog.arguments = args

                monthDialog.show(childFragmentManager, "Choice Month")
            }
        }
    }

    private fun subscribe() {
        mainViewModel.date.observe(viewLifecycleOwner) {
            binding.navigationBarLayout.titleTextView.text = getDateMonthYearString(
                it,
                resources.getStringArray(R.array.name_month)
            )
            viewModel.changeData(it)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->

                    if (uiState.isDateChange || uiState.isAllTransactionsLoaded) {
                        binding.calendarRecyclerView.itemAnimator = null
                        calendarAdapter.submitList(uiState.calendarDataItem)
                        transactionAdapter.submitList(uiState.transactionsDataItem)
                        binding.noTransactionsTextView.setVisible(uiState.transactionsDataItem.isEmpty())
                        binding.transactionRecyclerView.setVisible(uiState.transactionsDataItem.isNotEmpty())
                        if (uiState.isDateChange)
                            viewModel.onChangeDate()
                        if (uiState.isAllTransactionsLoaded)
                            viewModel.onAllTransactionsLoaded()
                    }

                    if (uiState.isReturn) {
                        findNavController().popBackStack()
                    }

                }
            }
        }
    }


    override fun choiceMonth(numberMonth: Int) {
        mainViewModel.setMonth(numberMonth)
    }
}