package com.fiz.mono.feature_calculator.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fiz.mono.R
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.databinding.FragmentCalculatorBinding
import kotlinx.coroutines.launch

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

        bind()
        bindListener()
        subscribe()
    }

    internal fun bind() {
        binding.apply {
            navigationBarLayout.backButton.setVisible(false)
            navigationBarLayout.actionButton.setVisible(false)
            navigationBarLayout.choiceImageButton.setVisible(false)
            navigationBarLayout.titleTextView.text = getString(R.string.calculator)
        }
    }

    private fun bindListener() {
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
                viewModel.onEvent(CalculatorUIEvent.ClickReset)
            }

            acCalendarImageButton.setOnClickListener {
                viewModel.onEvent(CalculatorUIEvent.ClickAC)
            }

            deleteCalendarImageButton.setOnClickListener {
                viewModel.onEvent(CalculatorUIEvent.ClickDelete)
            }
        }
    }

    private fun onNumberClick(view: View) {
        val button: Button = view as Button
        val number=button.text.toString()
        viewModel.onEvent(CalculatorUIEvent.ClickNumber(number))
    }

    private fun onOperationClick(view: View) {
        val button: Button = view as Button
        val operator=button.text.toString()
        viewModel.onEvent(CalculatorUIEvent.ClickOperator(operator))
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    binding.resultCalendarTextView.text = uiState.result
                    binding.operationCalendarTextView.text = uiState.history
                }
            }
        }
    }

}