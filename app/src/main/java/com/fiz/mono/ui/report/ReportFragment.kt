package com.fiz.mono.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.databinding.FragmentReportBinding
import com.fiz.mono.ui.input.InputViewModel
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
            viewModel.date.add(Calendar.DAY_OF_YEAR, -1)
            binding.dataRangeLayout.editTextDate.text =
                SimpleDateFormat("MMMM, yyyy").format(viewModel.date.time)
        }

        binding.dataRangeLayout.rightDateRangeImageButton.setOnClickListener {
            viewModel.date.add(Calendar.DAY_OF_YEAR, 1)
            binding.dataRangeLayout.editTextDate.text =
                SimpleDateFormat("MMMM, yyyy").format(viewModel.date.time)
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

        updateAdapter()
    }

    private fun updateAdapter() {
        adapter = TransactionsAdapter(viewModel.currency)
        val allTransactions = when (viewModel.tabSelectedReport) {
            0 -> TransactionStore.getAllTransactions()
            1 -> TransactionStore.getAllTransactions().filter { it.value < 0 }
            else -> TransactionStore.getAllTransactions().filter { it.value > 0 }
        }
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