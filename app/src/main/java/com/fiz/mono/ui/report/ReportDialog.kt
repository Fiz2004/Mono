package com.fiz.mono.ui.report

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.fiz.mono.R
import com.fiz.mono.databinding.DialogChoiceReportBinding
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.themeColor

class ReportDialog : DialogFragment() {
    private var _binding: DialogChoiceReportBinding? = null
    private val binding get() = _binding!!

    private var choice: Int = 0

    interface Choicer {
        fun choiceMonthly()
        fun choiceCategory()
    }

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
        _binding = DialogChoiceReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.monthlyTextView.setOnClickListener(::monthlyOnClickListener)
        binding.categoryTextView.setOnClickListener(::categoryOnClickListener)

        if (choice == ReportFragment.MONTHLY) {
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