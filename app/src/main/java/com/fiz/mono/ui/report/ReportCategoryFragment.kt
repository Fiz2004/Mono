package com.fiz.mono.ui.report

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.fiz.mono.App
import com.fiz.mono.R
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.categoryIcons
import com.fiz.mono.databinding.FragmentReportCategoryBinding
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.shared_adapters.TransactionsAdapter
import com.fiz.mono.util.getColorCompat
import kotlin.math.abs

class ReportCategoryFragment : Fragment() {
    private val args: ReportCategoryFragmentArgs by navArgs()

    private var _binding: FragmentReportCategoryBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: ReportCategoryViewModel by viewModels {
        ReportCategoryViewModelFactory(
            (requireActivity().application as App).categoryStore,
            (requireActivity().application as App).transactionStore
        )
    }

    private lateinit var adapter: TransactionsAdapter

    private var isExpense: Boolean = false
    private var category: CategoryItem? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportCategoryBinding.inflate(inflater, container, false)
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
        subscribe()
    }

    private fun subscribe() {
        viewModel.allCategoryExpense.observe(viewLifecycleOwner) {
            if (isExpense) {
                updateUI(it)
            }
        }
        viewModel.allCategoryIncome.observe(viewLifecycleOwner) {
            if (!isExpense) {
                updateUI(it)
            }
        }
        viewModel.allTransactions.observe(viewLifecycleOwner) {
            binding.valueReportCategoryTextView.text =
                if (!isExpense) {
                    "+"
                } else {
                    ""
                } +
                        mainViewModel.currency.value +
                        abs(it
                            .filter { it.nameCategory == category?.name }
                            .map { it.value }
                            .fold(0.0) { acc, d -> acc + d })
                            .toString()
        }
    }

    private fun updateUI(allCategoryTransaction: List<CategoryItem>) {
        category = allCategoryTransaction.find { it.id == args.id }

        val icon = categoryIcons.find { it.id == category?.mapImgSrc }

        icon?.imgSrc?.let { binding.iconImageView.setImageResource(it) }

        binding.iconTextView.text = category?.name

        adapter.submitList(
            viewModel.getTransactions(
                MONTH,
                mainViewModel.date.value!!,
                category?.name!!
            )
        )
    }

    private fun init() {
        adapter = TransactionsAdapter(mainViewModel.currency.value ?: "$", false)

        isExpense = args.type == SelectCategoryFragment.TYPE_EXPENSE
    }

    private fun bind() {
        binding.iconImageView.imageTintList = ColorStateList.valueOf(
            requireContext().getColorCompat(
                if (isExpense)
                    R.color.expense
                else
                    R.color.income
            )
        )

        binding.iconBackgroundImageView.imageTintList = ColorStateList.valueOf(
            requireContext().getColorCompat(
                if (isExpense)
                    R.color.expense
                else
                    R.color.income
            )
        )

        binding.valueReportCategoryTextView.setTextColor(
            requireContext().getColorCompat(
                if (isExpense)
                    R.color.expense
                else
                    R.color.income
            )
        )

        binding.transactionsRecyclerView.adapter = adapter
    }

    companion object {
        const val MONTH = 0
        const val WEEK = 1
    }
}