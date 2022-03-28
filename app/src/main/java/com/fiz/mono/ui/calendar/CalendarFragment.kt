package com.fiz.mono.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.App
import com.fiz.mono.R
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.databinding.FragmentCalendarBinding
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.shared_adapters.TransactionsAdapter
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.setVisible
import com.fiz.mono.util.themeColor
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: CalendarViewModel by viewModels(factoryProducer = viewModelInit())

    private val monthsTextView = emptyList<TextView>().toMutableList()

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var transactionAdapter: TransactionsAdapter

    private fun viewModelInit(): () -> CalendarViewModelFactory = {
        CalendarViewModelFactory(
            (requireActivity().application as App).categoryStore,
            (requireActivity().application as App).transactionStore
        )
    }

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

        calendarAdapter = CalendarAdapter(::calendarAdapterOnClickListener)
        transactionAdapter = mainViewModel.currency.value?.let {
            TransactionsAdapter(it, true, ::transactionAdapterOnClickListener)
        } ?: TransactionsAdapter("$", true, ::transactionAdapterOnClickListener)

        binding.apply {
            navigationBarLayout.backButton.setVisible(true)
            navigationBarLayout.actionButton.setVisible(false)
            navigationBarLayout.choiceImageButton.setVisible(true)

            navigationBarLayout.backButton.setOnClickListener(::backButtonOnClickListener)
            navigationBarLayout.choiceImageButton.setOnClickListener(::choiceMonthOnClickListener)
            calendarRecyclerView.adapter = calendarAdapter
            transactionRecyclerView.adapter = transactionAdapter

            monthsTextView.add(januaryTextView)
            monthsTextView.add(februaryTextView)
            monthsTextView.add(marchTextView)
            monthsTextView.add(aprilTextView)
            monthsTextView.add(mayTextView)
            monthsTextView.add(juneTextView)
            monthsTextView.add(julyTextView)
            monthsTextView.add(augustTextView)
            monthsTextView.add(septemberTextView)
            monthsTextView.add(octoberTextView)
            monthsTextView.add(novemberTextView)
            monthsTextView.add(decemberTextView)

            monthsTextView.forEach {
                it.setOnClickListener {
                    monthsOnClickListener(
                        monthsTextView.indexOf(
                            it
                        )
                    )
                }
            }
        }

        mainViewModel.date.observe(viewLifecycleOwner, ::dateObserve)

        viewModel.allTransaction.observe(viewLifecycleOwner) {
            dateObserve(mainViewModel.date.value)
        }
    }

    private fun monthsOnClickListener(numberMonth: Int) {
        mainViewModel.setMonth(numberMonth)
        binding.choiceMonthConstraintLayout.visibility = View.GONE
    }

    private fun choiceMonthOnClickListener(view: View?) {
        binding.choiceMonthConstraintLayout.visibility =
            if (binding.choiceMonthConstraintLayout.visibility == View.GONE)
                View.VISIBLE
            else
                View.GONE

        val currentMonth = mainViewModel.date.value?.get(Calendar.MONTH) ?: 0

        for (monthTextView in monthsTextView)
            if (monthsTextView.indexOf(monthTextView) == (currentMonth))
                monthTextView.setTextColor(requireContext().getColorCompat(R.color.blue))
            else
                monthTextView.setTextColor(requireContext().themeColor(androidx.appcompat.R.attr.colorPrimary))

    }

    private fun dateObserve(calendar: Calendar?) {
        binding.navigationBarLayout.titleTextView.text =
            SimpleDateFormat("MMMM, yyyy", Locale.getDefault()).format(calendar?.time!!)

        // Без этого присваивания при выборе декабря приложение крошится
        binding.calendarRecyclerView.itemAnimator = null

        val listCalendar = viewModel.getListCalendarDataItem(mainViewModel.date.value!!)
        calendarAdapter.submitList(listCalendar)

        val listTransactionsDataItem =
            viewModel.getListTransactionsDataItem(mainViewModel.date.value!!)

        binding.noTransactionsTextView.setVisible(listTransactionsDataItem.size == 0)
        binding.transactionRecyclerView.setVisible(listTransactionsDataItem.size != 0)

        transactionAdapter.submitList(listTransactionsDataItem)
    }

    private fun calendarAdapterOnClickListener(transactionsDay: TransactionsDay) {
        if (transactionsDay.day == 0) return

        mainViewModel.setDate(transactionsDay.day)
    }

    private fun transactionAdapterOnClickListener(transactionItem: TransactionItem) {
        val action =
            CalendarFragmentDirections
                .actionCalendarFragmentToInputFragment(transaction = transactionItem.id)
        findNavController().navigate(action)
    }

    private fun backButtonOnClickListener(view: View) {
        findNavController().popBackStack()
    }

}