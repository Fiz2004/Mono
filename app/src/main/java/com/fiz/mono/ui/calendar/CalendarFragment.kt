package com.fiz.mono.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.data.database.ItemDatabase
import com.fiz.mono.databinding.FragmentCalendarBinding
import com.fiz.mono.ui.MainViewModel
import java.text.SimpleDateFormat

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: CalendarViewModel by viewModels(factoryProducer = viewModelInit())

    private lateinit var adapter: CalendarAdapter

    private fun viewModelInit(): () -> CalendarViewModelFactory = {
        CalendarViewModelFactory(
            CategoryStore(
                ItemDatabase.getDatabase()?.categoryItemDao()!!
            ),
            TransactionStore(
                ItemDatabase.getDatabase()?.transactionItemDao()!!
            )
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

        binding.backButton.setOnClickListener(::backButtonOnClickListener)

        binding.titleTextView.text = SimpleDateFormat("MMММ, yyyy").format(mainViewModel.date.time)

        adapter = CalendarAdapter()
        binding.calendarRecyclerView.adapter = adapter

        viewModel.allTransaction.observe(viewLifecycleOwner) {
            val list =
                getDayWeeks().map { DataItem.DayWeekItem(it) } + viewModel.getTransactionsOfDays(mainViewModel.date)
                    .map {
                        DataItem.DayItem(TransactionsDay(it.day, it.expense, it.income))
                    }
            adapter.submitList(list)
        }
    }

    private fun getDayWeeks(): List<String> {
        return listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    }

    private fun backButtonOnClickListener(view: View) {
        findNavController().popBackStack()
    }

}