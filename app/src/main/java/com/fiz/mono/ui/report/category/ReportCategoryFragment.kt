package com.fiz.mono.ui.report.category

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
import com.fiz.mono.ui.MainViewModelFactory
import com.fiz.mono.ui.report.category.ReportCategoryUtils.getValuesForVerticalForMonth
import com.fiz.mono.ui.report.category.ReportCategoryUtils.getValuesForVerticalForWeek
import com.fiz.mono.ui.report.select.SelectCategoryFragment
import com.fiz.mono.ui.shared_adapters.TransactionsAdapter
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.themeColor
import com.google.android.material.button.MaterialButtonToggleGroup
import java.text.SimpleDateFormat
import java.util.*


class ReportCategoryFragment : Fragment() {
    private val args: ReportCategoryFragmentArgs by navArgs()

    private var _binding: FragmentReportCategoryBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (requireActivity().application as App).categoryStore,
            (requireActivity().application as App).transactionStore,
            requireActivity().getSharedPreferences(
                getString(R.string.preferences),
                AppCompatActivity.MODE_PRIVATE
            )
        )
    }
    private val viewModel: ReportCategoryViewModel by viewModels {
        ReportCategoryViewModelFactory(
            (requireActivity().application as App).categoryStore,
            (requireActivity().application as App).transactionStore
        )
    }

    private lateinit var adapter: TransactionsAdapter

    private var isExpense: Boolean = false
    private var category: CategoryItem? = null

    lateinit var list: MutableList<TransactionsDataItem>

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
            binding.valueReportCategoryTextView.text = viewModel.getValueReportCategory(
                it,
                mainViewModel.currency.value!!,
                isExpense,
                category?.name!!
            )
        }

        viewModel.reportFor.observe(viewLifecycleOwner) {
            drawGraph(it)
        }
    }

    private fun updateUI(allCategoryTransaction: List<CategoryItem>) {
        category = allCategoryTransaction.find { it.id == args.id }

        val icon = categoryIcons.find { it.id == category?.mapImgSrc }

        icon?.imgSrc?.let { binding.iconImageView.setImageResource(it) }

        binding.iconTextView.text = category?.name

        list = viewModel.getTransactions(
            MONTH,
            mainViewModel.date.value!!,
            category?.name!!
        )
        adapter.submitList(
            list
        )


    }


    private fun init() {
        adapter = TransactionsAdapter(mainViewModel.currency.value ?: "$", false)

        isExpense = args.type == SelectCategoryFragment.TYPE_EXPENSE
    }

    private fun bind() {
        val color = if (isExpense)
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
            drawGraph(viewModel.reportFor.value ?: MONTH)
        }
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

    private fun drawGraph(period: Int) {
        if (binding.graphImageView.width == 0 || binding.graphImageView.height == 0) return
        val width = binding.graphImageView.width
        val height = binding.graphImageView.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        if (period == MONTH) {
            drawLineMonth(canvas)
            drawTextMonth(canvas)
        } else {
            drawLineWeek(canvas)
            drawTextWeek(canvas)
        }
        binding.graphImageView.setImageBitmap(bitmap)
    }

    private fun drawLineMonth(canvas: Canvas) {
        val width = binding.graphImageView.width
        val height = binding.graphImageView.height
        val usefulHeight = height * 0.65f

        val paintFill = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFill.style = Paint.Style.FILL
        paintFill.shader = getShader(usefulHeight)
        paintFill.color = requireContext().getColorCompat(
            if (isExpense)
                R.color.expense
            else
                R.color.income
        )
        paintFill.strokeWidth = 0f * resources.displayMetrics.density

        val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG)
        paintStroke.style = Paint.Style.STROKE
        paintStroke.color = requireContext().getColorCompat(
            if (isExpense)
                R.color.expense
            else
                R.color.income
        )
        paintStroke.strokeWidth = 2f * resources.displayMetrics.density


        val valuesByMonth =
            getValuesForVerticalForMonth(
                viewModel.allTransactions.value, category?.name,
                Calendar.getInstance(), Calendar.getInstance()
            ).map {
                usefulHeight - usefulHeight * it
            }

        val stepWidth = width / 5f

        val pathFill = Path()
        val pathStroke = Path()
        pathFill.moveTo(0f, valuesByMonth[0].toFloat())
        pathStroke.moveTo(0f, valuesByMonth[0].toFloat())

        var cX = 0f
        for (n in 1..5) {
            cX += if (n == 1)
                stepWidth / 2f
            else
                stepWidth

            val stepHeight = valuesByMonth[n] - valuesByMonth[n - 1]
            pathFill.quadTo(
                cX - stepWidth / 2,
                (valuesByMonth[n] - (stepHeight / 2f)).toFloat(),
                cX,
                valuesByMonth[n].toFloat()
            )
            pathStroke.quadTo(
                cX - stepWidth / 2,
                (valuesByMonth[n] - (stepHeight / 2f)).toFloat(),
                cX,
                valuesByMonth[n].toFloat()
            )
        }
        pathStroke.lineTo(width.toFloat(), valuesByMonth[valuesByMonth.size - 1].toFloat())
        pathFill.lineTo(width.toFloat(), valuesByMonth[valuesByMonth.size - 1].toFloat())
        pathFill.lineTo(width.toFloat(), usefulHeight.toFloat())
        pathFill.lineTo(0f, usefulHeight.toFloat())
        pathFill.close()
        canvas.drawPath(pathFill, paintFill)
        canvas.drawPath(pathStroke, paintStroke)
    }

    private fun drawLineWeek(canvas: Canvas) {
        val width = binding.graphImageView.width
        val height = binding.graphImageView.height
        val usefulHeight = height * 0.65f

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.shader = getShader(usefulHeight)
        paint.color = requireContext().getColorCompat(
            if (isExpense)
                R.color.expense
            else
                R.color.income
        )
        paint.strokeWidth = 0f

        val valuesByWeek =
            getValuesForVerticalForWeek(
                viewModel.allTransactions.value, category?.name,
                Calendar.getInstance(), Calendar.getInstance()
            ).map {
                usefulHeight - usefulHeight * it
            }

        val stepWidth = width / 7f

        val path = Path()
        path.moveTo(0f, valuesByWeek[0].toFloat())

        var cX = 0f
        for (n in 1..7) {
            cX += if (n == 1)
                stepWidth / 2f
            else
                stepWidth

            val stepHeight = valuesByWeek[n] - valuesByWeek[n - 1]

            path.quadTo(
                cX - stepWidth / 2,
                (valuesByWeek[n] - (stepHeight / 2f)).toFloat(),
                cX,
                valuesByWeek[n].toFloat()
            )
        }
        path.lineTo(width.toFloat(), valuesByWeek[valuesByWeek.size - 1].toFloat())
        path.lineTo(width.toFloat(), usefulHeight.toFloat())
        path.lineTo(0f, usefulHeight.toFloat())
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawTextMonth(
        canvas: Canvas
    ) {
        val width = binding.graphImageView.width
        val height = binding.graphImageView.height

        val paintFont = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFont.textSize = spToPx(16f, requireContext())
        paintFont.color =
            requireContext().themeColor(com.google.android.material.R.attr.colorSecondary)
        paintFont.textAlign = Paint.Align.CENTER

        var currentX = 0f
        val monthDate = Calendar.getInstance()
        monthDate.add(Calendar.MONTH, -6)
        val stepWidth = width / 5f

        for (n in 0 until 5) {
            currentX += if (n == 0)
                stepWidth / 2f
            else
                stepWidth

            monthDate.add(Calendar.MONTH, 1)
            val nameMonth = SimpleDateFormat(
                "LLL",
                Locale.getDefault()
            ).format(monthDate.time)

            canvas.drawText("$nameMonth", currentX, height - paintFont.textSize, paintFont)
        }
    }

    private fun drawTextWeek(
        canvas: Canvas
    ) {
        val width = binding.graphImageView.width
        val height = binding.graphImageView.height

        val paintFont = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFont.textSize = spToPx(16f, requireContext())
        paintFont.color =
            requireContext().themeColor(com.google.android.material.R.attr.colorSecondary)
        paintFont.textAlign = Paint.Align.CENTER

        var currentX = 0f
        val dayDate = Calendar.getInstance()
        dayDate.add(Calendar.DATE, -8)
        val stepWidth = width / 7f

        for (n in 0 until 7) {
            currentX += if (n == 0)
                stepWidth / 2f
            else
                stepWidth

            dayDate.add(Calendar.DATE, 1)
            val nameMonth = SimpleDateFormat(
                "EE",
                Locale.getDefault()
            ).format(dayDate.time)

            canvas.drawText("$nameMonth", currentX, height - paintFont.textSize, paintFont)
        }
    }

    private fun spToPx(sp: Float, context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics
        )
    }


    private fun getShader(usefulHeight: Float) = LinearGradient(
        0f, 0f, 0f, usefulHeight.toFloat(),
        requireContext().getColorCompat(
            if (isExpense)
                R.color.expense_gradient_0
            else
                R.color.income_gradient_0
        ),
        requireContext().getColorCompat(
            if (isExpense)
                R.color.expense_gradient_1
            else
                R.color.income_gradient_1
        ),
        Shader.TileMode.REPEAT
    )

    companion object {
        const val MONTH = 0
        const val WEEK = 1
    }
}