package com.fiz.mono.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.databinding.FragmentReportBinding
import com.fiz.mono.ui.input.InputViewModel
import com.fiz.mono.ui.input.getCurrencyFormat
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*


class ReportFragment : Fragment() {
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InputViewModel by activityViewModels()

    private lateinit var adapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dataRangeLayout.editTextDate.text =
            SimpleDateFormat("MMMM, yyyy").format(viewModel.date.time)

        binding.dataRangeLayout.leftDateRangeImageButton.setOnClickListener {
            viewModel.date.add(Calendar.MONTH, -1)
            binding.dataRangeLayout.editTextDate.text =
                SimpleDateFormat("MMMM, yyyy").format(viewModel.date.time)
            updateUI()
            updateAdapter()
        }

        binding.dataRangeLayout.rightDateRangeImageButton.setOnClickListener {
            viewModel.date.add(Calendar.MONTH, 1)
            binding.dataRangeLayout.editTextDate.text =
                SimpleDateFormat("MMMM, yyyy").format(viewModel.date.time)
            updateUI()
            updateAdapter()
        }

        binding.allExpenseIncomeTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewModel.tabSelectedReport = tab.position
                    updateAdapter()
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })

        updateUI()
        updateAdapter()
    }

    private fun updateUI() {
        val allTransactions = TransactionStore.getAllTransactions()
        val currentBalance = allTransactions.map { it.value }.fold(0.0) { acc, d -> acc + d }
        binding.valueReportTextView.text =
            getCurrencyFormat(viewModel.currency, currentBalance, false)

        val currentYear = viewModel.date.get(Calendar.YEAR)
        val currentMonth = viewModel.date.get(Calendar.MONTH) + 1
        val allTransactionsForYear =
            allTransactions.filter { SimpleDateFormat("yyyy").format(it.date.time) == currentYear.toString() } as MutableList<TransactionItem>
        val allTransactionsForMonth =
            allTransactionsForYear.filter { SimpleDateFormat("M").format(it.date.time) == currentMonth.toString() } as MutableList<TransactionItem>

        val currentIncome = allTransactionsForMonth.filter { it.value > 0 }.map { it.value }
            .fold(0.0) { acc, d -> acc + d }
        val currentExpense = allTransactionsForMonth.filter { it.value < 0 }.map { it.value }
            .fold(0.0) { acc, d -> acc + d }

        binding.incomeValueReportTextView.text =
            getCurrencyFormat(viewModel.currency, currentIncome, false)
        binding.expenseValueReportTextView.text =
            getCurrencyFormat(viewModel.currency, currentExpense, false)

        binding.expenseIncomeValueReportTextView.text =
            getCurrencyFormat(viewModel.currency, currentIncome - currentExpense, true)

        val copyDate = viewModel.date
        copyDate.add(Calendar.MONTH, -1)
        val prevYear = copyDate.get(Calendar.YEAR)
        val prevMonth = copyDate.get(Calendar.MONTH) + 1
        copyDate.add(Calendar.MONTH, 1)

        val allTransactionsPrevMonthForYear =
            allTransactions.filter { SimpleDateFormat("yyyy").format(it.date.time) == prevYear.toString() } as MutableList<TransactionItem>
        val allTransactionsPrevMonthForMonth =
            allTransactionsPrevMonthForYear.filter { SimpleDateFormat("M").format(it.date.time) == prevMonth.toString() } as MutableList<TransactionItem>

        val prevIncome = allTransactionsPrevMonthForMonth.filter { it.value > 0 }.map { it.value }
            .fold(0.0) { acc, d -> acc + d }
        val prevExpense = allTransactionsPrevMonthForMonth.filter { it.value < 0 }.map { it.value }
            .fold(0.0) { acc, d -> acc + d }

        binding.previousBalanceValueReportTextView.text =
            getCurrencyFormat(viewModel.currency, prevIncome - prevExpense, false)
    }

    private fun updateAdapter() {
        adapter = TransactionsAdapter(viewModel.currency)

        var allTransactions = when (viewModel.tabSelectedReport) {
            0 -> TransactionStore.getAllTransactions().toMutableList()
            1 -> TransactionStore.getAllTransactions().filter { it.value < 0 }
            else -> TransactionStore.getAllTransactions().filter { it.value > 0 }
        }
        val currentYear = viewModel.date.get(Calendar.YEAR)
        val currentMonth = viewModel.date.get(Calendar.MONTH) + 1
        allTransactions =
            allTransactions.filter { SimpleDateFormat("yyyy").format(it.date.time) == currentYear.toString() }
        allTransactions =
            allTransactions.filter { SimpleDateFormat("M").format(it.date.time) == currentMonth.toString() }
        allTransactions.sortedByDescending { it.date }

        val groupTransactions =
            allTransactions.groupBy { SimpleDateFormat("MMM dd, yyyy").format(it.date.time) }
        val items = mutableListOf<DataItem>()
        for (date in groupTransactions) {
            val expense =
                date.value.filter { it.value < 0 }.map { it.value }.fold(0.0) { acc, d -> acc + d }
            val income =
                date.value.filter { it.value > 0 }.map { it.value }.fold(0.0) { acc, d -> acc + d }
            items += DataItem.InfoDayHeaderItem(InfoDay(date.key, expense, income))
            items += date.value.map { DataItem.InfoTransactionItem(it) }
        }
        adapter.submitList(items)
        binding.transactionsRecyclerView.adapter = adapter
    }

}