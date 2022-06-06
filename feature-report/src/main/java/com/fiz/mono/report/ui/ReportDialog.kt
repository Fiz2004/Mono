package com.fiz.mono.report.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.fiz.mono.base.android.utils.getColorCompat
import com.fiz.mono.base.android.utils.themeColor
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.report.databinding.DialogChoiceReportBinding
import com.fiz.mono.report.ui.ReportViewModel.Companion.MONTHLY

class ReportDialog : DialogFragment() {
    private var choice: Int = 0

    interface Choicer {
        fun choiceMonthly()
        fun choiceCategory()
    }

    private lateinit var binding: DialogChoiceReportBinding

    // Be sure to install in the calling fragment
    var choicer: Choicer? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        choice = arguments?.getInt("currentChoice") ?: 0
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.window?.attributes?.gravity = Gravity.TOP.or(Gravity.END)
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogChoiceReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.monthlyTextView.setOnClickListener(::monthlyOnClickListener)
        binding.categoryTextView.setOnClickListener(::categoryOnClickListener)

        if (choice == MONTHLY) {
            binding.monthlyTextView.setTextColor(requireContext().getColorCompat(R.color.blue))
            binding.categoryTextView.setTextColor(requireContext().themeColor(androidx.appcompat.R.attr.colorPrimary))
        } else {
            binding.monthlyTextView.setTextColor(requireContext().themeColor(androidx.appcompat.R.attr.colorPrimary))
            binding.categoryTextView.setTextColor(requireContext().getColorCompat(R.color.blue))
        }
    }

    private fun categoryOnClickListener(view: View) {
        choicer?.choiceCategory()
        dismiss()
    }

    private fun monthlyOnClickListener(view: View) {
        choicer?.choiceMonthly()
        dismiss()
    }

}