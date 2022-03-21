package com.fiz.mono.ui.currency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentCurrencyBinding
import com.fiz.mono.ui.MainViewModel

class CurrencyFragment : Fragment() {
    private var _binding: FragmentCurrencyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.USDRadioButton.setOnClickListener(::onRadioButtonClicked)
        binding.JPYRadioButton.setOnClickListener(::onRadioButtonClicked)
        binding.CRCRadioButton.setOnClickListener(::onRadioButtonClicked)
        binding.GBPRadioButton.setOnClickListener(::onRadioButtonClicked)
        binding.AZNRadioButton.setOnClickListener(::onRadioButtonClicked)
        binding.ALLRadioButton.setOnClickListener(::onRadioButtonClicked)
        binding.BGNRadioButton.setOnClickListener(::onRadioButtonClicked)
        binding.VNDRadioButton.setOnClickListener(::onRadioButtonClicked)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        when (viewModel.currency) {
            "$" -> binding.USDRadioButton.isChecked = true
            "¥" -> binding.JPYRadioButton.isChecked = true
            "₡" -> binding.CRCRadioButton.isChecked = true
            "£" -> binding.GBPRadioButton.isChecked = true
            "₼" -> binding.AZNRadioButton.isChecked = true
            "€" -> binding.ALLRadioButton.isChecked = true
            "лв" -> binding.BGNRadioButton.isChecked = true
            "đ" -> binding.VNDRadioButton.isChecked = true
        }
    }

    private fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            binding.USDRadioButton.isChecked = false
            binding.JPYRadioButton.isChecked = false
            binding.CRCRadioButton.isChecked = false
            binding.GBPRadioButton.isChecked = false
            binding.AZNRadioButton.isChecked = false
            binding.ALLRadioButton.isChecked = false
            binding.BGNRadioButton.isChecked = false
            binding.VNDRadioButton.isChecked = false

            when (view.getId()) {
                R.id.USDRadioButton ->
                    if (checked) {
                        binding.USDRadioButton.isChecked = true
                        viewModel.currency = binding.iconUSDRadioButton.text.toString()
                    }
                R.id.JPYRadioButton ->
                    if (checked) {
                        binding.JPYRadioButton.isChecked = true
                        viewModel.currency = binding.iconJPYRadioButton.text.toString()
                    }
                R.id.CRCRadioButton ->
                    if (checked) {
                        binding.CRCRadioButton.isChecked = true
                        viewModel.currency = binding.iconCRCRadioButton.text.toString()
                    }
                R.id.GBPRadioButton ->
                    if (checked) {
                        binding.GBPRadioButton.isChecked = true
                        viewModel.currency = binding.iconGBPRadioButton.text.toString()
                    }
                R.id.AZNRadioButton ->
                    if (checked) {
                        binding.AZNRadioButton.isChecked = true
                        viewModel.currency = binding.iconAZNRadioButton.text.toString()
                    }
                R.id.ALLRadioButton ->
                    if (checked) {
                        binding.ALLRadioButton.isChecked = true
                        viewModel.currency = binding.iconALLRadioButton.text.toString()
                    }
                R.id.BGNRadioButton ->
                    if (checked) {
                        binding.BGNRadioButton.isChecked = true
                        viewModel.currency = binding.iconBGNRadioButton.text.toString()
                    }
                R.id.VNDRadioButton ->
                    if (checked) {
                        binding.VNDRadioButton.isChecked = true
                        viewModel.currency = binding.iconVNDRadioButton.text.toString()
                    }
            }
            val sharedPreferences = requireActivity().getSharedPreferences(
                getString(R.string.preferences),
                AppCompatActivity.MODE_PRIVATE
            ).edit()
            sharedPreferences.putString("currency", viewModel.currency)
            sharedPreferences.apply()
        }
    }
}