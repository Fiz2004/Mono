package com.fiz.mono.report.ui.category

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
import androidx.fragment.app.viewModels
import com.fiz.mono.base.android.adapters.TransactionsAdapter
import com.fiz.mono.base.android.utils.getColorCompat
import com.fiz.mono.base.android.utils.launchAndRepeatWithViewLifecycle
import com.fiz.mono.base.android.utils.themeColor
import com.fiz.mono.feature.report.R
import com.fiz.mono.feature.report.databinding.FragmentReportCategoryBinding
import com.fiz.mono.navigation.CategoryInfoArgs
import com.fiz.mono.navigation.navigationData
import com.google.android.material.button.MaterialButtonToggleGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportCategoryFragment : Fragment() {

    private val viewModel: ReportCategoryViewModel by viewModels()

    private val adapter: TransactionsAdapter by lazy {
        TransactionsAdapter(
            viewModel.viewState.value.currency,
            false
        )
    }

    private val binding get() = _binding!!
    private var _binding: FragmentReportCategoryBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setupUI()
        observeViewStateUpdates()
    }

    private fun init() {
        val categoryInfoArgs = navigationData as? CategoryInfoArgs
        categoryInfoArgs?.let {
            viewModel.start(it.type, it.id)
        }
    }

    private fun setupUI() {
        val color = if (viewModel.viewState.value.isExpense)
            com.fiz.mono.common.ui.resources.R.color.expense
        else
            com.fiz.mono.common.ui.resources.R.color.income

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

        binding.transactionsRecyclerView.adapter = adapter

        binding.graphImageView.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            viewModel.onGraphImageViewLayoutChange()
        }
    }

    private fun observeViewStateUpdates() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collect { newState ->
                updateScreenState(newState)
            }
        }
    }

    private fun updateScreenState(newState: ReportCategoryViewState) {
        binding.periodTextView.text = getString(newState.period)
        binding.valueReportCategoryTextView.text = newState.getValueReportCategory(
            viewModel.viewState.value.currency
        )
        adapter.submitList(newState.getTransactions())

        newState.category?.let {
            binding.iconImageView.setImageResource(it.imgSrc)
            binding.iconTextView.text = it.name
        }

        if (newState.isCanGraph)
            drawGraph()
    }

    private fun drawGraph() {
        if (binding.graphImageView.width == 0 || binding.graphImageView.height == 0) return

        val width = binding.graphImageView.width
        val height = binding.graphImageView.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val color = requireContext().getColorCompat(
            if (viewModel.viewState.value.isExpense)
                com.fiz.mono.common.ui.resources.R.color.expense
            else
                com.fiz.mono.common.ui.resources.R.color.income
        )
        val density = resources.displayMetrics.density
        val textSize = spToPx(16f, requireContext())
        val colorText =
            requireContext().themeColor(com.google.android.material.R.attr.colorSecondary)
        val colorForShader1 = requireContext().getColorCompat(
            if (viewModel.viewState.value.isExpense)
                com.fiz.mono.common.ui.resources.R.color.expense_gradient_0
            else
                com.fiz.mono.common.ui.resources.R.color.income_gradient_0
        )
        val colorForShader2 = requireContext().getColorCompat(
            if (viewModel.viewState.value.isExpense)
                com.fiz.mono.common.ui.resources.R.color.expense_gradient_1
            else
                com.fiz.mono.common.ui.resources.R.color.income_gradient_1
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.transactionsRecyclerView.adapter = null
        _binding = null
    }

}