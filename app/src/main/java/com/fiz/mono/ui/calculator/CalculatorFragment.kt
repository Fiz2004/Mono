package com.fiz.mono.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentCalculatorBinding
import com.fiz.mono.util.setVisible


class CalculatorFragment : Fragment() {
    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CalculatorViewModel by viewModels()

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

        binding.navigationBarLayout.backButton.setVisible(false)
        binding.navigationBarLayout.actionButton.setVisible(false)
        binding.navigationBarLayout.choiceImageButton.setVisible(false)
        binding.navigationBarLayout.titleTextView.text = getString(R.string.calculator)

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
            viewModel.resetData()
            updateUI()
        }

        binding.acCalendarImageButton.setOnClickListener {
            viewModel.resetData()
            updateUI()
        }
        binding.deleteCalendarImageButton.setOnClickListener {
            if (binding.resultCalendarTextView.text == "") return@setOnClickListener
            viewModel.deleteLastSymbol()
            updateUI()
        }
    }

    private fun updateUI() {
        binding.resultCalendarTextView.text = viewModel.getCurrentOperation()
        binding.operationCalendarTextView.text = viewModel.getHistory()
    }

    private fun onNumberClick(view: View) {
        val button: Button = view as Button
        viewModel.numberClick(button.text.toString())
        updateUI()
    }

    private fun onOperationClick(view: View) {
        val button: Button = view as Button
        viewModel.operatorClick(button.text.toString())
        updateUI()
    }

}