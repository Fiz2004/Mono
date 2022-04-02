package com.fiz.mono.ui.calendar

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.fiz.mono.R
import com.fiz.mono.databinding.DialogChoiceMonthBinding
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.themeColor

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
        month = arguments?.getInt("currentMonth") ?: 0
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
                if (bindingMonthsTextView.indexOf(monthTextView) == (month))
                    monthTextView.setTextColor(requireContext().getColorCompat(R.color.blue))
                else
                    monthTextView.setTextColor(requireContext().themeColor(androidx.appcompat.R.attr.colorPrimary))


            bindingMonthsTextView.forEachIndexed { index, textView ->
                textView.text = resources.getStringArray(R.array.name_month)[index]
                textView.setOnClickListener { monthsOnClickListener(index) }
            }
        }
    }

    private fun monthsOnClickListener(numberMonth: Int) {
        choicer?.choiceMonth(numberMonth)
        dismiss()
    }
}