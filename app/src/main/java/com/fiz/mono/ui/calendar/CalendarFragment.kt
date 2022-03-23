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
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.data.database.ItemDatabase
import com.fiz.mono.databinding.FragmentCalendarBinding
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.shared_adapters.TransactionsAdapter
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: CalendarViewModel by viewModels(factoryProducer = viewModelInit())

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var transactionAdapter: TransactionsAdapter

    private fun viewModelInit(): () -> CalendarViewModelFactory = {
        val database = ItemDatabase.getDatabase()
        database?.let {
            CalendarViewModelFactory(
                CategoryStore(
                    database.categoryItemDao()
                ),
                TransactionStore(
                    database.transactionItemDao()
                )
            )
        } ?: throw Error("Database not available")
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

        binding.titleTextView.text =
            SimpleDateFormat("MMMM, yyyy", Locale.US).format(mainViewModel.date.time)

        calendarAdapter = CalendarAdapter(::calendarAdapterOnClickListener)
        binding.calendarRecyclerView.adapter = calendarAdapter

        transactionAdapter = TransactionsAdapter(mainViewModel.currency)
        binding.transactionRecyclerView.adapter = transactionAdapter

        viewModel.allTransaction.observe(viewLifecycleOwner, ::allTransactionObserve)
    }

    private fun allTransactionObserve(allTransaction: List<TransactionItem>) {
        calendarAdapter.submitList(viewModel.getListCalendarDataItem(mainViewModel.date))
        val listTransactionsDataItem = viewModel.getListTransactionsDataItem(mainViewModel.date)

        binding.noTransactionsTextView.visibility = if (listTransactionsDataItem.size == 0)
            View.VISIBLE else View.GONE

        binding.transactionRecyclerView.visibility = if (listTransactionsDataItem.size == 0)
            View.GONE else View.VISIBLE

        transactionAdapter.submitList(listTransactionsDataItem)
    }

    private fun calendarAdapterOnClickListener(transactionsDay: TransactionsDay) {
        if (transactionsDay.day == 0) return

        val date = mainViewModel.date
        date.set(Calendar.DATE, transactionsDay.day)

        allTransactionObserve(listOf())
    }

    private fun backButtonOnClickListener(view: View) {
        findNavController().popBackStack()
    }

}