package com.fiz.mono.report.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.fiz.mono.base.android.utils.getColorCompat
import com.fiz.mono.base.android.utils.themeColor
import com.fiz.mono.feature.report.databinding.DialogChoiceReportBinding

class ReportDialog : DialogFragment() {
    private val binding get() = _binding!!

    private var _binding: DialogChoiceReportBinding? = null
    private val viewModel: ReportViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.window?.let {
            it.attributes?.gravity = Gravity.TOP.or(Gravity.END)
            it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogChoiceReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            monthlyTextView.apply {
                setOnClickListener(::monthlyOnClickListener)

                val color =
                    if (viewModel.viewState.value.categorySelectedReport == CategoryReport.Monthly)
                        requireContext().getColorCompat(com.fiz.mono.common.ui.resources.R.color.blue)
                    else
                        requireContext().themeColor(androidx.appcompat.R.attr.colorPrimary)

                setTextColor(color)
            }

            categoryTextView.apply {
                setOnClickListener(::categoryOnClickListener)

                val color =
                    if (viewModel.viewState.value.categorySelectedReport == CategoryReport.Monthly)
                        requireContext().themeColor(androidx.appcompat.R.attr.colorPrimary)
                    else
                        requireContext().getColorCompat(com.fiz.mono.common.ui.resources.R.color.blue)

                setTextColor(color)
            }
        }
    }

    private fun categoryOnClickListener(view: View) {
        viewModel.onEvent(ReportEvent.CategoryClicked)
        dismiss()
    }

    private fun monthlyOnClickListener(view: View) {
        viewModel.onEvent(ReportEvent.MonthlyClicked)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}