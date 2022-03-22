package com.fiz.mono.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.fiz.mono.databinding.FragmentSettingsBinding
import com.fiz.mono.ui.pin_password.PINPasswordFragment

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.modeSwitch.setOnCheckedChangeListener(::modeOnClickListener)

        binding.categoryCircleRightImageView.setOnClickListener(::categoryOnClickListener)
        binding.categoryTextView.setOnClickListener(::categoryOnClickListener)
        binding.categoryIconImageView.setOnClickListener(::categoryOnClickListener)

        binding.currencyCircleRightImageView.setOnClickListener(::currencyOnClickListener)
        binding.currencyTextView.setOnClickListener(::currencyOnClickListener)
        binding.currencyIconImageView.setOnClickListener(::currencyOnClickListener)

        binding.pinPasswordCircleRightImageView.setOnClickListener(::pinPasswordOnClickListener)
        binding.pinPasswordTextView.setOnClickListener(::pinPasswordOnClickListener)
        binding.pinPasswordIconImageView.setOnClickListener(::pinPasswordOnClickListener)

        binding.reminderCircleRightImageView.setOnClickListener(::reminderOnClickListener)
        binding.reminderTextView.setOnClickListener(::reminderOnClickListener)
        binding.reminderIconImageView.setOnClickListener(::reminderOnClickListener)
        updateUI()
    }

    private fun reminderOnClickListener(view: View) {
        val action =
            SettingsFragmentDirections
                .actionSettingsFragmentToReminderFragment()
        view.findNavController().navigate(action)
    }

    private fun pinPasswordOnClickListener(view: View) {
        val action =
            SettingsFragmentDirections
                .actionSettingsFragmentToPINPasswordFragment(PINPasswordFragment.SETTINGS)
        view.findNavController().navigate(action)
    }

    private fun currencyOnClickListener(view: View) {
        val action =
            SettingsFragmentDirections
                .actionSettingsFragmentToCurrencyFragment()
        view.findNavController().navigate(action)
    }

    private fun categoryOnClickListener(view: View) {
        val action =
            SettingsFragmentDirections
                .actionSettingsFragmentToCategoryFragment("", "", "")
        view.findNavController().navigate(action)
    }

    private fun modeOnClickListener(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun updateUI() {
        binding.modeSwitch.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }

}

