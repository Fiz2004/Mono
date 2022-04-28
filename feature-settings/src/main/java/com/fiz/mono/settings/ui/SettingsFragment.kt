package com.fiz.mono.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.settings.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind()
        bindListener()
        updateUI()
    }

    private fun bindListener() {
        binding.apply {
            modeSwitch.setOnCheckedChangeListener(::modeOnClickListener)

            categoryCircleRightImageView.setOnClickListener(::categoryOnClickListener)
            categoryTextView.setOnClickListener(::categoryOnClickListener)
            categoryIconImageView.setOnClickListener(::categoryOnClickListener)

            currencyCircleRightImageView.setOnClickListener(::currencyOnClickListener)
            currencyTextView.setOnClickListener(::currencyOnClickListener)
            currencyIconImageView.setOnClickListener(::currencyOnClickListener)

            pinPasswordCircleRightImageView.setOnClickListener(::pinPasswordOnClickListener)
            pinPasswordTextView.setOnClickListener(::pinPasswordOnClickListener)
            pinPasswordIconImageView.setOnClickListener(::pinPasswordOnClickListener)

            reminderCircleRightImageView.setOnClickListener(::reminderOnClickListener)
            reminderTextView.setOnClickListener(::reminderOnClickListener)
            reminderIconImageView.setOnClickListener(::reminderOnClickListener)

            deleteIconImageView.setOnClickListener(::deleteOnClickListener)
            deleteTextView.setOnClickListener(::deleteOnClickListener)
        }
    }

    private fun bind() {
        binding.apply {
            navigationBarLayout.backButton.setVisible(false)
            navigationBarLayout.actionButton.setVisible(false)
            navigationBarLayout.choiceImageButton.setVisible(false)
            navigationBarLayout.titleTextView.text = getString(R.string.settings)
        }
    }


    private fun deleteOnClickListener(view: View) {
        viewModel.clickDelete()
        Toast.makeText(requireContext(), R.string.delete_all_data, Toast.LENGTH_LONG).show()
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
                .actionToPINPasswordFragment("settings")
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
                .actionToCategoryFragment()
        view.findNavController().navigate(action)
    }

    private fun modeOnClickListener(buttonView: CompoundButton, isChecked: Boolean) {
        viewModel.setThemeLight(!isChecked)
        val mode = if (isChecked)
            AppCompatDelegate.MODE_NIGHT_YES
        else
            AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun updateUI() {
        binding.modeSwitch.isChecked = viewModel.themeLight
    }

}