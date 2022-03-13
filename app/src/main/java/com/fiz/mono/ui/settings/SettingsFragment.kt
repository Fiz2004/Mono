package com.fiz.mono.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.fiz.mono.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.modeSwitch.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        binding.modeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.categoryCircleRightImageView.setOnClickListener {
            val action =
                SettingsFragmentDirections
                    .actionSettingsFragmentToCategoryFragment("", 0, "")
            view.findNavController().navigate(action)
        }

        binding.currencyCircleRightImageView.setOnClickListener {
            val action =
                SettingsFragmentDirections
                    .actionSettingsFragmentToCurrencyFragment()
            view.findNavController().navigate(action)
        }

        binding.pinPasswordCircleRightImageView.setOnClickListener {
            val action =
                SettingsFragmentDirections
                    .actionSettingsFragmentToPINPasswordFragment()
            view.findNavController().navigate(action)
        }

        binding.reminderCircleRightImageView.setOnClickListener {
            val action =
                SettingsFragmentDirections
                    .actionSettingsFragmentToReminderFragment()
            view.findNavController().navigate(action)
        }
    }

}

