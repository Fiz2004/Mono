package com.fiz.mono.calculator.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fiz.mono.base.android.utils.launchAndRepeatWithViewLifecycle
import com.fiz.mono.base.android.utils.setVisible
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.feature.calculator.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment() {

    private val viewModel: CalculatorViewModel by viewModels()

    private lateinit var binding: FragmentCalculatorBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupListeners()
        observeViewStateUpdates()
    }

    private fun setupUI() {
        binding.navigationBarLayout.apply {
            backButton.setVisible(false)
            actionButton.setVisible(false)
            choiceImageButton.setVisible(false)
            titleTextView.text = getString(R.string.calculator)
        }
    }

    private fun setupListeners() {
        binding.apply {
            oneCalendarImageButton.setOnClickListener(::onNumberClick)
            twoCalendarImageButton.setOnClickListener(::onNumberClick)
            threeCalendarImageButton.setOnClickListener(::onNumberClick)
            fourCalendarImageButton.setOnClickListener(::onNumberClick)
            fiveCalendarImageButton.setOnClickListener(::onNumberClick)
            sixCalendarImageButton.setOnClickListener(::onNumberClick)
            sevenCalendarImageButton.setOnClickListener(::onNumberClick)
            eightCalendarImageButton.setOnClickListener(::onNumberClick)
            nineCalendarImageButton.setOnClickListener(::onNumberClick)
            zeroCalendarImageButton.setOnClickListener(::onNumberClick)
            pointCalendarImageButton.setOnClickListener(::onNumberClick)

            timesCalendarImageButton.setOnClickListener(::onOperationClick)
            dividedCalendarImageButton.setOnClickListener(::onOperationClick)
            plusCalendarImageButton.setOnClickListener(::onOperationClick)
            minusCalendarImageButton.setOnClickListener(::onOperationClick)
            equalsCalendarImageButton.setOnClickListener(::onOperationClick)

            resetCalendarImageButton.setOnClickListener {
                viewModel.onEvent(CalculatorEvent.ResetClicked)
            }

            acCalendarImageButton.setOnClickListener {
                viewModel.onEvent(CalculatorEvent.ACClicked)
            }

            deleteCalendarImageButton.setOnClickListener {
                viewModel.onEvent(CalculatorEvent.DeleteClicked)
            }
        }
    }

    private fun onNumberClick(view: View) {
        val button: Button = view as Button
        val number=button.text.toString()
        viewModel.onEvent(CalculatorEvent.NumberClicked(number))
    }

    private fun onOperationClick(view: View) {
        val button: Button = view as Button
        val operator=button.text.toString()
        viewModel.onEvent(CalculatorEvent.OperatorClicked(operator))
    }

    private fun observeViewStateUpdates() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collect { newState ->
                updateScreenState(newState)
            }
        }
    }

    private fun updateScreenState(newState: CalculatorViewState) {
        binding.apply {
            resultCalendarTextView.text = newState.result
            operationCalendarTextView.text = newState.history
        }
    }

}