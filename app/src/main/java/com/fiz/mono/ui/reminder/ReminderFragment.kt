package com.fiz.mono.ui.reminder

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentReminderBinding
import com.fiz.mono.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings

const val REQUEST_CODE_REMINDER = 0

@AndroidEntryPoint
@WithFragmentBindings
class ReminderFragment : Fragment() {

    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReminderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)

        viewModel.createChannel(
            getString(R.string.mono_notification_channel_id),
            getString(R.string.mono_notification_channel_name),
            getString(R.string.mono_will_reminder_to_note_transaction_on_this_time_everyday),
            requireActivity().application as Application
        )

        viewModel.init(requireActivity().application as Application)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            navigationBarLayout.backButton.setVisible(true)
            navigationBarLayout.actionButton.setVisible(false)
            navigationBarLayout.choiceImageButton.setVisible(false)
            navigationBarLayout.titleTextView.text = getString(R.string.reminder)

            hoursEditText.doOnTextChanged { text, start, before, count ->
                try {
                    if (text.toString().toInt() > 23)
                        throw Error("")
                    else
                        viewModel.setHours(text.toString().toInt())
                } catch (e: Exception) {
                    if (text.toString() != "") {
                        hoursEditText.error = "error"
                    }
                    viewModel.hoursError()
                }
            }

            minutesEditText.doOnTextChanged { text, start, before, count ->
                try {
                    if (text.toString().toInt() > 59)
                        throw Error("")
                    else
                        viewModel.setMinutes(text.toString().toInt())
                } catch (e: Exception) {
                    if (text.toString() != "") {
                        hoursEditText.error = "error"
                    }
                    viewModel.minutesError()
                }
            }

            val hours = requireActivity().getSharedPreferences(
                requireActivity().getString(R.string.preferences),
                AppCompatActivity.MODE_PRIVATE
            ).getInt("notify hours", 0)
            val minutes = requireActivity().getSharedPreferences(
                requireActivity().getString(R.string.preferences),
                AppCompatActivity.MODE_PRIVATE
            ).getInt("notify minutes", 0)

            if (hours != 0 && minutes != 0) {
                viewModel.notify.value = true
            }

            navigationBarLayout.backButton.setOnClickListener(::backButtonOnClickListener)
            setReminderButton.setOnClickListener(::setReminderButtonOnClickListener)
        }

        viewModel.isCanReminder.observe(viewLifecycleOwner) {
            binding.setReminderButton.isEnabled = it
        }

        viewModel.hours.observe(viewLifecycleOwner) {
            if (binding.hoursEditText.text.toString() != it.toString() && binding.hoursEditText.text.toString() != "")
                binding.hoursEditText.setText(
                    if (it != 0)
                        it.toString()
                    else
                        ""
                )

        }
        viewModel.minutes.observe(viewLifecycleOwner) {
            if (binding.minutesEditText.text.toString() != it.toString() && binding.hoursEditText.text.toString() != "")
                binding.minutesEditText.setText(
                    if (it != 0)
                        it.toString()
                    else
                        ""
                )
        }

        viewModel.notify.observe(viewLifecycleOwner) {
            if (it) {
                val hours = requireActivity().getSharedPreferences(
                    requireActivity().getString(R.string.preferences),
                    AppCompatActivity.MODE_PRIVATE
                ).getInt("notify hours", 0)
                val minutes = requireActivity().getSharedPreferences(
                    requireActivity().getString(R.string.preferences),
                    AppCompatActivity.MODE_PRIVATE
                ).getInt("notify minutes", 0)

                binding.setReminderButton.isEnabled = true
                binding.setReminderButton.isCheckable = true
                binding.hoursEditText.isEnabled = false
                binding.minutesEditText.isEnabled = false
                binding.setReminderButton.text = getString(R.string.remove_reminder)
                binding.setReminderButton.invalidate()
                viewModel.setHours(hours)
                viewModel.setMinutes(minutes)
            } else {
                binding.setReminderButton.isCheckable = false
                binding.hoursEditText.isEnabled = true
                binding.minutesEditText.isEnabled = true
                binding.setReminderButton.text = getString(R.string.set_reminder)
            }
        }
    }

    private fun setReminderButtonOnClickListener(view: View?) {
        if (viewModel.notify.value == true) {
            viewModel.cancelNotification(requireActivity().application as Application)
        } else {
            viewModel.setAlarm(requireActivity().application)
            findNavController().popBackStack()
        }
    }

    private fun backButtonOnClickListener(view: View) {
        findNavController().popBackStack()
    }
}