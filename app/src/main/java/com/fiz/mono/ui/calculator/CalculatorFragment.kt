package com.fiz.mono.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.fiz.mono.databinding.FragmentCalculatorBinding


class CalculatorFragment : Fragment() {
    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    var number1: String = ""
    var number2: String = ""
    var operation: String = ""
    var lastOperation = "="


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.oneCalendarImageButton.setOnClickListener(::onNumberClick)
        binding.twoCalendarImageButton.setOnClickListener(::onNumberClick)
        binding.threeCalendarImageButton.setOnClickListener(::onNumberClick)
        binding.fourCalendarImageButton.setOnClickListener(::onNumberClick)
        binding.fiveCalendarImageButton.setOnClickListener(::onNumberClick)
        binding.sixCalendarImageButton.setOnClickListener(::onNumberClick)
        binding.sevenCalendarImageButton.setOnClickListener(::onNumberClick)
        binding.eightCalendarImageButton.setOnClickListener(::onNumberClick)
        binding.nineCalendarImageButton.setOnClickListener(::onNumberClick)
        binding.zeroCalendarImageButton.setOnClickListener(::onNumberClick)
        binding.pointCalendarImageButton.setOnClickListener(::onNumberClick)

        binding.timesCalendarImageButton.setOnClickListener(::onOperationClick)
        binding.dividedCalendarImageButton.setOnClickListener(::onOperationClick)
        binding.plusCalendarImageButton.setOnClickListener(::onOperationClick)
        binding.minusCalendarImageButton.setOnClickListener(::onOperationClick)
        binding.equalsCalendarImageButton.setOnClickListener(::onOperationClick)

        binding.resetCalendarImageButton.setOnClickListener {
            number1 = ""
            number2 = ""
            operation = ""
            lastOperation = "="
            binding.resultCalendarTextView.text = ""
            binding.operationCalendarTextView.text = ""
        }
        binding.acCalendarImageButton.setOnClickListener {
            number1 = ""
            number2 = ""
            operation = ""
            lastOperation = "="
            binding.resultCalendarTextView.text = ""
            binding.operationCalendarTextView.text = ""
        }
        binding.deleteCalendarImageButton.setOnClickListener {
            if (binding.resultCalendarTextView.text == "") return@setOnClickListener
            if (operation == "") {
                number1 = number1.substring(0, number1.length - 1)
            } else {
                number2 = number2.substring(0, number2.length - 1)
            }
            binding.resultCalendarTextView.text = number1 + operation + number2
        }
    }

    fun onNumberClick(view: View) {
        val button: Button = view as Button
        if (operation == "") {
            if (button.text != "." || !number1.contains("."))
                number1 += button.text
        } else {
            if (button.text != "." || !number2.contains("."))
                number2 += button.text
        }
        binding.resultCalendarTextView.text = number1 + operation + number2
        if (lastOperation == "=" && operation != "") {
            operation = ""
        }
    }

    fun onOperationClick(view: View) {
        val button: Button = view as Button
        operation = button.text.toString()
        if (operation == "=") {
            try {
                performOperation()
            } catch (ex: NumberFormatException) {
                binding.operationCalendarTextView.text = ""
                operation = ""
                binding.resultCalendarTextView.text = number1 + operation + number2
            }
            operation = ""
            lastOperation = operation
            return
        }
        lastOperation = operation
        binding.resultCalendarTextView.text = number1 + operation + number2
    }

    private fun performOperation() {
        var result: String = ""
        when (lastOperation) {
            "/" -> result = if (number2 == "0") {
                number1 = result
                number2 = ""
                ""
            } else {
                (number1.toDouble() / number2.toDouble()).toString()
            }
            "x" -> result = (number1.toDouble() * number2.toDouble()).toString()
            "+" -> result = (number1.toDouble() + number2.toDouble()).toString()
            "-" -> result = (number1.toDouble() - number2.toDouble()).toString()
        }
        binding.operationCalendarTextView.text = number1 + lastOperation + number2
        if (result.length > 8)
            result = result.substring(8)
        if (result.toDouble() % 1.0 == 0.0)
            result = result.toDouble().toInt().toString()
        number1 = result
        number2 = ""
        if (result.length > 8)
            result = result.substring(8)
        binding.resultCalendarTextView.text = result
    }

}