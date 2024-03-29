package com.fiz.mono.currency.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.base.android.utils.launchAndRepeatWithViewLifecycle
import com.fiz.mono.currency.databinding.FragmentCurrencyBinding
import com.google.android.material.radiobutton.MaterialRadioButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CurrencyFragment : Fragment() {
    private val viewModel: CurrencyViewModel by viewModels()

    private var currencyRadioButton =
        mutableMapOf<String, MaterialRadioButton>()

    private lateinit var binding: FragmentCurrencyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupListeners()
        observeViewStateUpdates()
    }

    private fun setupUI() {
        currencyRadioButton["$"] = binding.USDRadioButton
        currencyRadioButton["₽"] = binding.RUBRadioButton
        currencyRadioButton["¥"] = binding.JPYRadioButton
        currencyRadioButton["₡"] = binding.CRCRadioButton
        currencyRadioButton["£"] = binding.GBPRadioButton
        currencyRadioButton["₼"] = binding.AZNRadioButton
        currencyRadioButton["€"] = binding.ALLRadioButton
        currencyRadioButton["лв"] = binding.BGNRadioButton
        currencyRadioButton["đ"] = binding.VNDRadioButton
    }

    private fun setupListeners() {
        currencyRadioButton.values.forEach {
            it.setOnClickListener { view ->
                if (view is RadioButton) {
                    val checked = view.isChecked

                    if (checked) {
                        val selectEntriesRadioButton =
                            currencyRadioButton.entries.find { it.value.id == view.id }

                        selectEntriesRadioButton?.let {
                            viewModel.saveCurrency(it.key)
                        }
                    }
                }
            }
        }

        binding.navigationBarLayout.setOnClickListenerBackButton {
            findNavController().popBackStack()
        }
    }

    private fun observeViewStateUpdates() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collectLatest { newState ->
                updateScreenState(newState)
            }
        }
    }

    private fun updateScreenState(newState: CurrencyViewState) {
        currencyRadioButton.values.forEach { it.isChecked = false }

        currencyRadioButton[newState.currency]?.isChecked = true
    }
}