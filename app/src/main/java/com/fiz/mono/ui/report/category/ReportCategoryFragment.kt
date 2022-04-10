package com.fiz.mono.ui.report.category

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.fiz.mono.App
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentReportCategoryBinding
import com.fiz.mono.ui.MainPreferencesViewModel
import com.fiz.mono.ui.shared_adapters.TransactionsAdapter
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.themeColor
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.coroutines.launch

class ReportCategoryFragment : Fragment() {
    private val args: ReportCategoryFragmentArgs by navArgs()

    private var _binding: FragmentReportCategoryBinding? = null
    private val binding get() = _binding!!

    private val mainPreferencesViewModel: MainPreferencesViewModel by activityViewModels()
    private val viewModel: ReportCategoryViewModel by viewModels {
        ReportCategoryViewModelFactory(
            (requireActivity().application as App).categoryRepository,
            (requireActivity().application as App).transactionStore
        )
    }

    private lateinit var adapter: TransactionsAdapter

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

    private fun init() {
        adapter = TransactionsAdapter(
            mainPreferencesViewModel.currency.value ?: "$",
            false
        )

        viewModel.init(args.type, args.id)
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    uiState.category?.let {
                        binding.iconImageView.setImageResource(it.imgSrc)
                        binding.iconTextView.text = it.name
                        binding.thisTextView.text = getString(
                            if (uiState.reportFor == ReportCategoryUiState.MONTH) R.string.this_month else
                                R.string.this_week
                        )
                        binding.valueReportCategoryTextView.text = uiState.getValueReportCategory(
                            mainPreferencesViewModel.currency.value!!
                        )
                        adapter.submitList(uiState.getTransactions())

                        if (uiState.isCanGraph)
                            drawGraph(uiState.reportFor)
                    }
                }
            }
        }
    }

    private fun bind() {
        val color = if (viewModel.uiState.value.isExpense)
            R.color.expense
        else
            R.color.income

        binding.iconImageView.imageTintList = ColorStateList.valueOf(
            requireContext().getColorCompat(color)
        )

        binding.iconBackgroundImageView.imageTintList = ColorStateList.valueOf(
            requireContext().getColorCompat(color)
        )

        binding.valueReportCategoryTextView.setTextColor(
            requireContext().getColorCompat(color)
        )

        binding.monthWeekToggleButton.addOnButtonCheckedListener(::monthWeekOnButtonCheckedListener)

        binding.transactionsRecyclerView.adapter = adapter

        binding.graphImageView.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            viewModel.onGraphImageViewLayoutChange()
        }
    }

    private fun drawGraph(period: Int) {
        if (binding.graphImageView.width == 0 || binding.graphImageView.height == 0) return
        val width = binding.graphImageView.width
        val height = binding.graphImageView.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val color = requireContext().getColorCompat(
            if (viewModel.uiState.value.isExpense)
                R.color.expense
            else
                R.color.income
        )
        val density = resources.displayMetrics.density
        val textSize = spToPx(16f, requireContext())
        val colorText =
            requireContext().themeColor(com.google.android.material.R.attr.colorSecondary)
        val colorForShader1 = requireContext().getColorCompat(
            if (viewModel.uiState.value.isExpense)
                R.color.expense_gradient_0
            else
                R.color.income_gradient_0
        )
        val colorForShader2 = requireContext().getColorCompat(
            if (viewModel.uiState.value.isExpense)
                R.color.expense_gradient_1
            else
                R.color.income_gradient_1
        )
        viewModel.drawGraph(
            canvas,
            width,
            height,
            color,
            density,
            textSize,
            colorText,
            colorForShader1,
            colorForShader2
        )
        binding.graphImageView.setImageBitmap(bitmap)
    }

    private fun spToPx(sp: Float, context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics
        )
    }

    private fun monthWeekOnButtonCheckedListener(
        toggleButton: MaterialButtonToggleGroup,
        checkedId: Int,
        isChecked: Boolean
    ) {
        when (checkedId) {
            R.id.monthToggleButton -> {
                if (isChecked)
                    viewModel.clickMonthToggleButton()
            }
            R.id.weekToggleButton -> {
                if (isChecked)
                    viewModel.clickWeekToggleButton()
            }
        }
    }
}