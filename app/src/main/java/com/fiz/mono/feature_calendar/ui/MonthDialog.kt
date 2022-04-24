package com.fiz.mono.core.ui.calendar

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.util.getColorCompat
import com.fiz.mono.core.util.themeColor
import com.fiz.mono.databinding.DialogChoiceMonthBinding

class MonthDialog : DialogFragment() {
    private var _binding: DialogChoiceMonthBinding? = null
    private val binding get() = _binding!!

    private val bindingMonthsTextView = emptyList<TextView>().toMutableList()

    private var month: Int = 0

    interface Choicer {
        fun choiceMonth(numberMonth: Int)
    }

    // Be sure to install in the calling fragment
    var choicer: Choicer? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        month = arguments?.getInt("currentMonth") ?: 1
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
        _binding = DialogChoiceMonthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            bindingMonthsTextView.add(januaryTextView)
            bindingMonthsTextView.add(februaryTextView)
            bindingMonthsTextView.add(marchTextView)
            bindingMonthsTextView.add(aprilTextView)
            bindingMonthsTextView.add(mayTextView)
            bindingMonthsTextView.add(juneTextView)
            bindingMonthsTextView.add(julyTextView)
            bindingMonthsTextView.add(augustTextView)
            bindingMonthsTextView.add(septemberTextView)
            bindingMonthsTextView.add(octoberTextView)
            bindingMonthsTextView.add(novemberTextView)
            bindingMonthsTextView.add(decemberTextView)

            for (monthTextView in bindingMonthsTextView)
                if (bindingMonthsTextView.indexOf(monthTextView) == (month - 1))
                    monthTextView.setTextColor(requireContext().getColorCompat(R.color.blue))
                else
                    monthTextView.setTextColor(requireContext().themeColor(androidx.appcompat.R.attr.colorPrimary))


            bindingMonthsTextView.forEachIndexed { index, textView ->
                textView.text = resources.getStringArray(R.array.name_month)[index]
                textView.setOnClickListener { monthsOnClickListener(index + 1) }
            }
        }
    }

    private fun monthsOnClickListener(numberMonth: Int) {
        choicer?.choiceMonth(numberMonth)
        dismiss()
    }
}