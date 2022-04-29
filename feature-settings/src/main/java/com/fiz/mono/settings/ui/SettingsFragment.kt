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
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.navigation.navigate
import com.fiz.mono.settings.R
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
            navigationBarLayout.titleTextView.text =
                getString(com.fiz.mono.common.ui.resources.R.string.settings)
        }
    }


    private fun deleteOnClickListener(view: View) {
        viewModel.clickDelete()
        Toast.makeText(
            requireContext(),
            com.fiz.mono.common.ui.resources.R.string.delete_all_data,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun reminderOnClickListener(view: View) {
        navigate(R.id.action_settingsFragment_to_reminderFragment)
    }

    private fun pinPasswordOnClickListener(view: View) {
        navigate(R.id.action_settingsFragment_to_PINPasswordFragment, data = "settings")
    }

    private fun currencyOnClickListener(view: View) {
        navigate(R.id.action_settingsFragment_to_currencyFragment)
    }

    private fun categoryOnClickListener(view: View) {
        navigate(R.id.action_settingsFragment_to_categoryFragment)
    }

    private fun modeOnClickListener(buttonView: CompoundButton, isChecked: Boolean) {
        val mode = if (isChecked)
            AppCompatDelegate.MODE_NIGHT_YES
        else
            AppCompatDelegate.MODE_NIGHT_NO

        if (AppCompatDelegate.getDefaultNightMode() == mode) return

        viewModel.clickSwitchTheme(mode)

        AppCompatDelegate.setDefaultNightMode(mode)
    }

    // Надо проверить другие режимы
    // Need to check other modes
    private fun updateUI() {
        binding.modeSwitch.isChecked = viewModel.theme == AppCompatDelegate.MODE_NIGHT_YES
    }

}