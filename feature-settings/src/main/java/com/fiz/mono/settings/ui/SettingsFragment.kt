package com.fiz.mono.settings.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fiz.mono.base.android.utils.setVisible
import com.fiz.mono.navigation.navigate
import com.fiz.mono.settings.R
import com.fiz.mono.settings.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        updateUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.navigationBarLayout.apply {
            backButton.setVisible(false)
            actionButton.setVisible(false)
            choiceImageButton.setVisible(false)
            titleTextView.text = getString(com.fiz.mono.common.ui.resources.R.string.settings)
        }
    }

    private fun setupListeners() {
        binding.apply {
            modeSwitch.setOnCheckedChangeListener { _, isChecked -> modeOnClickListener(isChecked) }

            categoryCircleRightImageView.setOnClickListener { categoryOnClickListener() }
            categoryTextView.setOnClickListener { categoryOnClickListener() }
            categoryIconImageView.setOnClickListener { categoryOnClickListener() }

            currencyCircleRightImageView.setOnClickListener { currencyOnClickListener() }
            currencyTextView.setOnClickListener { currencyOnClickListener() }
            currencyIconImageView.setOnClickListener { currencyOnClickListener() }

            pinPasswordCircleRightImageView.setOnClickListener { pinPasswordOnClickListener() }
            pinPasswordTextView.setOnClickListener { pinPasswordOnClickListener() }
            pinPasswordIconImageView.setOnClickListener { pinPasswordOnClickListener() }

            reminderCircleRightImageView.setOnClickListener { reminderOnClickListener() }
            reminderTextView.setOnClickListener { reminderOnClickListener() }
            reminderIconImageView.setOnClickListener { reminderOnClickListener() }

            deleteIconImageView.setOnClickListener { deleteOnClickListener() }
            deleteTextView.setOnClickListener { deleteOnClickListener() }
        }
    }

    private fun deleteOnClickListener() {
        viewModel.clickDelete()
        Toast.makeText(
            requireContext(),
            com.fiz.mono.common.ui.resources.R.string.delete_all_data,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun reminderOnClickListener() {
        navigate(R.id.action_settingsFragment_to_reminderFragment)
    }

    private fun pinPasswordOnClickListener() {
        navigate(R.id.action_settingsFragment_to_PINPasswordFragment, data = "settings")
    }

    private fun currencyOnClickListener() {
        navigate(R.id.action_settingsFragment_to_currencyFragment)
    }

    private fun categoryOnClickListener() {
        navigate(R.id.action_settingsFragment_to_categoryFragment)
    }

    private fun modeOnClickListener(isChecked: Boolean) {
        val mode = if (isChecked)
            AppCompatDelegate.MODE_NIGHT_YES
        else
            AppCompatDelegate.MODE_NIGHT_NO

        val theme = if (isChecked)
            Configuration.UI_MODE_NIGHT_YES
        else
            Configuration.UI_MODE_NIGHT_NO

        viewModel.clickSwitchTheme(theme)

        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun updateUI() {
        binding.modeSwitch.isChecked = viewModel.theme == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}