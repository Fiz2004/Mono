package com.fiz.mono.feature_report.ui.category

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
import androidx.navigation.fragment.navArgs
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.ui.shared_adapters.TransactionsAdapter
import com.fiz.mono.core.util.getColorCompat
import com.fiz.mono.core.util.launchAndRepeatWithViewLifecycle
import com.fiz.mono.core.util.themeColor
import com.fiz.mono.databinding.FragmentReportCategoryBinding
import com.google.android.material.button.MaterialButtonToggleGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportCategoryFragment : Fragment() {

    private val args: ReportCategoryFragmentArgs by navArgs()

    private val mainPreferencesViewModel: com.fiz.mono.core.ui.MainPreferencesViewModel by activityViewModels()

    private val viewModel: ReportCategoryViewModel by viewModels()

    private val adapter: TransactionsAdapter by lazy {
        TransactionsAdapter(
            mainPreferencesViewModel.currency.value ?: "$",
            false
        )
    }

    private lateinit var binding: FragmentReportCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        bind()
        subscribe()
    }

    private fun init() {
        viewModel.start(args.type, args.id)
    }

    private fun subscribe() {
        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collect { uiState ->
                binding.periodTextView.text = getString(uiState.period)
                binding.valueReportCategoryTextView.text = uiState.getValueReportCategory(
                    mainPreferencesViewModel.currency.value ?: "$"
                )
                adapter.submitList(uiState.getTransactions())

                uiState.category?.let {
                    binding.iconImageView.setImageResource(it.imgSrc)
                    binding.iconTextView.text = it.name
                }

                if (uiState.isCanGraph)
                    drawGraph()
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

        binding.monthWeekToggleButton.addOnButtonCheckedListener { toggleButton: MaterialButtonToggleGroup,
                                                                   checkedId: Int,
                                                                   isChecked: Boolean ->

            when (checkedId) {
                com.fiz.mono.R.id.monthToggleButton -> {
                    if (isChecked)
                        viewModel.clickMonthToggleButton()
                }
                com.fiz.mono.R.id.weekToggleButton -> {
                    if (isChecked)
                        viewModel.clickWeekToggleButton()
                }
            }
        }

        binding.transactionsRecyclerView.adapter = adapter

        binding.graphImageView.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            viewModel.onGraphImageViewLayoutChange()
        }
    }

    private fun drawGraph() {
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
}