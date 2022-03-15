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
            SimpleDateFormat("MMM dd, yyyy (EEE)").format(viewModel.date.time)

        binding.dataRangeLayout.leftDateRangeImageButton.setOnClickListener {
            viewModel.date.add(Calendar.DAY_OF_YEAR, -1)
            binding.dataRangeLayout.editTextDate.text =
                SimpleDateFormat("MMM dd, yyyy (EEE)").format(viewModel.date.time)
        }

        binding.dataRangeLayout.rightDateRangeImageButton.setOnClickListener {
            viewModel.date.add(Calendar.DAY_OF_YEAR, 1)
            binding.dataRangeLayout.editTextDate.text =
                SimpleDateFormat("MMM dd, yyyy (EEE)").format(viewModel.date.time)
        }

        adapter = TransactionsAdapter(viewModel.currency)
        adapter.submitList(TransactionStore.getAllTransactions())
        binding.transactionsRecyclerView.adapter = adapter
    }

}