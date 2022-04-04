package com.fiz.mono.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.App
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentCalendarBinding
import com.fiz.mono.ui.MainPreferencesViewModel
import com.fiz.mono.ui.MainPreferencesViewModelFactory
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.shared_adapters.TransactionsAdapter
import com.fiz.mono.util.TimeUtils.getDateMonthYearString
import com.fiz.mono.util.setVisible
import java.util.*

class CalendarFragment : Fragment(), MonthDialog.Choicer {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CalendarViewModel by viewModels {
        CalendarViewModelFactory(
            (requireActivity().application as App).transactionStore
        )
    }

    private val mainViewModel: MainViewModel by activityViewModels()

    private val mainPreferencesViewModel: MainPreferencesViewModel by activityViewModels {
        MainPreferencesViewModelFactory(
            requireActivity().getSharedPreferences(
                getString(R.string.preferences),
                AppCompatActivity.MODE_PRIVATE
            )
        )
    }

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
                (requireActivity().application as App).categoryIconStore,
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
                val currentMonth = mainViewModel.date.value?.get(Calendar.MONTH) ?: 0
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
            viewModel.changeData()
        }

        viewModel.allTransactions.observe(viewLifecycleOwner) {
            // Без этого присваивания при выборе декабря приложение крошится
            binding.calendarRecyclerView.itemAnimator = null

            val listCalendar = viewModel.getListCalendarDataItem(mainViewModel.date.value)
            calendarAdapter.submitList(listCalendar)

            val listTransactionsDataItem =
                viewModel.getListTransactionsDataItem(mainViewModel.date.value)

            binding.noTransactionsTextView.setVisible(listTransactionsDataItem.isEmpty())
            binding.transactionRecyclerView.setVisible(listTransactionsDataItem.isNotEmpty())

            transactionAdapter.submitList(listTransactionsDataItem)
        }
    }


    override fun choiceMonth(numberMonth: Int) {
        mainViewModel.setMonth(numberMonth)
    }
}