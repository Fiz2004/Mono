package com.fiz.mono.reminder.ui

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.AlarmManagerCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.base.android.utils.launchAndRepeatWithViewLifecycle
import com.fiz.mono.base.android.utils.setVisible
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.feature.reminder.databinding.FragmentReminderBinding
import com.fiz.mono.reminder.utils.cancelNotifications
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val REQUEST_CODE_REMINDER = 0

@AndroidEntryPoint
class ReminderFragment : Fragment() {

    private val viewModel: ReminderViewModel by viewModels()

    private lateinit var binding: FragmentReminderBinding

    @Inject
    lateinit var notifyIntent: Intent

    @Inject
    lateinit var notifyPendingIntent: PendingIntent

    @Inject
    lateinit var alarmManager: AlarmManager

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationChannel: NotificationChannel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReminderBinding.inflate(inflater, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            navigationBarLayout.backButton.setVisible(true)
            navigationBarLayout.actionButton.setVisible(false)
            navigationBarLayout.choiceImageButton.setVisible(false)
            navigationBarLayout.titleTextView.text = getString(R.string.reminder)

            viewModel.start()

            hoursEditText.doAfterTextChanged {
                viewModel.setHours(it.toString())
            }

            minutesEditText.doAfterTextChanged {
                viewModel.setMinutes(it.toString())
            }

            navigationBarLayout.backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            setReminderButton.setOnClickListener {
                notificationManager.cancelNotifications()

                if (viewModel.viewState.value.isNotifyInstalled) {
                    alarmManager.cancel(notifyPendingIntent)

                    viewModel.cancelNotification()
                    viewModel.resetTime()
                } else {

                    AlarmManagerCompat.setExactAndAllowWhileIdle(
                        alarmManager,
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        viewModel.getTriggerTime(),
                        notifyPendingIntent
                    )
                    viewModel.saveTime()
                    viewModel.onNotify()

                    findNavController().popBackStack()
                }
            }
        }

        observeViewStateUpdates()
    }

    private fun observeViewStateUpdates() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collect { newState ->
                updateScreenState(newState)
            }
        }
    }

    private fun updateScreenState(newState: ReminderViewState) {
        binding.hoursEditText.isEnabled = !newState.isNotifyInstalled
        binding.hoursEditText.error = if (newState.isErrorHourEditText)
            getString(R.string.invalid_number)
        else
            null
        if (binding.hoursEditText.text.toString() != newState.timeForReminder.hour)
            binding.hoursEditText.setText(newState.timeForReminder.hour)

        binding.minutesEditText.isEnabled = !newState.isNotifyInstalled
        binding.minutesEditText.error = if (newState.isErrorMinuteEditText)
            getString(R.string.invalid_number)
        else
            null
        if (binding.minutesEditText.text.toString() != newState.timeForReminder.minute)
            binding.minutesEditText.setText(newState.timeForReminder.minute)

        val textForButtonReminder =
            if (newState.isNotifyInstalled) R.string.remove_reminder else R.string.set_reminder
        binding.setReminderButton.text = getString(textForButtonReminder)
        binding.setReminderButton.isActivated = newState.isNotifyInstalled
        binding.setReminderButton.isEnabled = newState.isCanReminder
    }
}