package com.fiz.mono.core.ui.currency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.databinding.FragmentCurrencyBinding

class CurrencyFragment : Fragment() {
    private val mainPreferencesViewModel: com.fiz.mono.core.ui.MainPreferencesViewModel by activityViewModels()

    private var currencyRadioButton =
        mutableMapOf<String, com.google.android.material.radiobutton.MaterialRadioButton>()

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

        bind()
    }

    private fun bind() {
        currencyRadioButton["$"] = binding.USDRadioButton
        currencyRadioButton["¥"] = binding.JPYRadioButton
        currencyRadioButton["₡"] = binding.CRCRadioButton
        currencyRadioButton["£"] = binding.GBPRadioButton
        currencyRadioButton["₼"] = binding.AZNRadioButton
        currencyRadioButton["€"] = binding.ALLRadioButton
        currencyRadioButton["лв"] = binding.BGNRadioButton
        currencyRadioButton["đ"] = binding.VNDRadioButton

        currencyRadioButton[mainPreferencesViewModel.currency.value]?.isChecked = true

        binding.navigationBarLayout.backButton.setVisible(true)
        binding.navigationBarLayout.actionButton.setVisible(false)
        binding.navigationBarLayout.choiceImageButton.setVisible(false)
        binding.navigationBarLayout.titleTextView.text = getString(R.string.currency)

        currencyRadioButton.values.forEach { it.setOnClickListener(::onRadioButtonClicked) }

        binding.navigationBarLayout.backButton.setOnClickListener(::backButtonOnClickListener)
    }

    private fun backButtonOnClickListener(view: View) {
        findNavController().popBackStack()
    }

    private fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            currencyRadioButton.values.forEach { it.isChecked = false }
            if (checked) {
                val selectEntriesRadioButton = currencyRadioButton.entries.find { it.value.id == view.id }
                selectEntriesRadioButton?.let {
                    it.value.isChecked = true
                    mainPreferencesViewModel.setCurrency(it.key)
                }
            }
        }
    }
}