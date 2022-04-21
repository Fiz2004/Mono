package com.fiz.mono.core.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.fiz.mono.R
import com.fiz.mono.core.data.data_source.CategoryDataSource
import com.fiz.mono.core.data.data_source.TransactionDataSource
import com.fiz.mono.core.ui.MainPreferencesViewModel
import com.fiz.mono.core.ui.pin_password.PINPasswordFragment
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val mainPreferencesViewModel: MainPreferencesViewModel by activityViewModels()

    @Inject
    lateinit var categoryDataSource: CategoryDataSource

    @Inject
    lateinit var transactionDataSource: TransactionDataSource

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
        CoroutineScope(Dispatchers.Default).launch {
            categoryDataSource.deleteAll(requireContext())
            transactionDataSource.deleteAll()
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), R.string.delete_all_data, Toast.LENGTH_LONG).show()
            }
        }
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
                .actionToPINPasswordFragment(PINPasswordFragment.SETTINGS)
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
        if (isChecked) {
            mainPreferencesViewModel.setThemeLight(false)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            mainPreferencesViewModel.setThemeLight(true)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun updateUI() {
        binding.modeSwitch.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }

}

