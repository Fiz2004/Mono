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
                viewModel.resetData()
            }

            acCalendarImageButton.setOnClickListener {
                viewModel.resetData()
            }

            deleteCalendarImageButton.setOnClickListener {
                if (binding.resultCalendarTextView.text == "")
                    return@setOnClickListener
                viewModel.deleteLastSymbol()
            }
        }
    }

    private fun onNumberClick(view: View) {
        val button: Button = view as Button
        viewModel.numberClick(button.text.toString())
    }

    private fun onOperationClick(view: View) {
        val button: Button = view as Button
        viewModel.operatorClick(button.text.toString())
    }

    private fun subscribe() {
        viewModel.result.observe(viewLifecycleOwner) {
            binding.resultCalendarTextView.text = it
        }

        viewModel.history.observe(viewLifecycleOwner) {
            binding.operationCalendarTextView.text = it
        }
    }

}