package com.fiz.mono.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.fiz.mono.R
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.data.database.ItemDatabase
import com.fiz.mono.databinding.FragmentReportBinding
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.getCurrencyFormat
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.themeColor
import java.text.SimpleDateFormat
import java.util.*


class ReportFragment : Fragment() {
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: ReportViewModel by viewModels {
        ReportViewModelFactory(
            TransactionStore(
                ItemDatabase.getDatabase()?.transactionItemDao()!!
            )
        )
    }

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

        binding.dataRangeLayout.dateTextView.text =
            SimpleDateFormat("LLLL, yyyy", Locale.US).format(mainViewModel.date.time)

        binding.dataRangeLayout.leftDateRangeImageButton.setOnClickListener {
            mainViewModel.date.add(Calendar.MONTH, -1)
            binding.dataRangeLayout.dateTextView.text =
                SimpleDateFormat("LLLL, yyyy", Locale.US).format(mainViewModel.date.time)
            allTransactionsObserve(listOf())
        }

        binding.dataRangeLayout.rightDateRangeImageButton.setOnClickListener {
            mainViewModel.date.add(Calendar.MONTH, 1)
            binding.dataRangeLayout.dateTextView.text =
                SimpleDateFormat("LLLL, yyyy", Locale.US).format(mainViewModel.date.time)
            allTransactionsObserve(listOf())
        }

        binding.allExpenseIncomeToggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            when (checkedId) {
                R.id.toggle1 -> {
                    if (isChecked)
                        mainViewModel.tabSelectedReport = 0
                }
                R.id.toggle2 -> {
                    if (isChecked)
                        mainViewModel.tabSelectedReport = 1
                }
                R.id.toggle3 -> {
                    if (isChecked)
                        mainViewModel.tabSelectedReport = 2
                }
            }
            if (isChecked) {
                adapter.submitList(
                    viewModel.getTransactions(
                        mainViewModel.tabSelectedReport,
                        mainViewModel.date
                    )
                )
            }
        }

        binding.choiceReportImageButton.setOnClickListener {
            if (binding.choiceReportConstraintLayout.visibility == View.GONE) {
                binding.choiceReportConstraintLayout.visibility = View.VISIBLE
                updateMenu()
            } else {
                binding.choiceReportConstraintLayout.visibility = View.GONE
            }

        }

        binding.monthlyTextView.setOnClickListener {
            mainViewModel.categorySelectedReport = 0
            binding.choiceReportConstraintLayout.visibility = View.GONE
        }

        binding.categoryTextView.setOnClickListener {
            mainViewModel.categorySelectedReport = 1
            binding.choiceReportConstraintLayout.visibility = View.GONE
        }

        viewModel.allTransactions.observe(viewLifecycleOwner, ::allTransactionsObserve)

        adapter = TransactionsAdapter(mainViewModel.currency)
        binding.transactionsRecyclerView.adapter = adapter
    }

    private fun allTransactionsObserve(allTransactions: List<TransactionItem>) {
        binding.valueReportTextView.text =
            getCurrencyFormat(mainViewModel.currency, viewModel.getCurrentBalance(), false)

        binding.incomeValueReportTextView.text =
            getCurrencyFormat(
                mainViewModel.currency,
                viewModel.getCurrentIncome(mainViewModel.date),
                false
            )
        binding.expenseValueReportTextView.text =
            getCurrencyFormat(
                mainViewModel.currency,
                viewModel.getCurrentExpense(mainViewModel.date),
                false
            )

        binding.expenseIncomeValueReportTextView.text =
            getCurrencyFormat(
                mainViewModel.currency,
                viewModel.getCurrentIncome(mainViewModel.date) + viewModel.getCurrentExpense(
                    mainViewModel.date
                ),
                true
            )

        binding.previousBalanceValueReportTextView.text =
            getCurrencyFormat(
                mainViewModel.currency,
                viewModel.getPreviousBalanceValue(mainViewModel.date),
                false
            )

        adapter.submitList(
            viewModel.getTransactions(
                mainViewModel.tabSelectedReport,
                mainViewModel.date
            )
        )
    }

    private fun updateMenu() {
        if (mainViewModel.categorySelectedReport == 0) {
            binding.monthlyTextView.setTextColor(requireContext().getColorCompat(R.color.blue))
            binding.categoryTextView.setTextColor(requireContext().themeColor(androidx.appcompat.R.attr.colorPrimary))
        } else {
            binding.monthlyTextView.setTextColor(requireContext().themeColor(androidx.appcompat.R.attr.colorPrimary))
            binding.categoryTextView.setTextColor(requireContext().getColorCompat(R.color.blue))
        }
    }
}