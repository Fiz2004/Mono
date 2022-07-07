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
import androidx.annotation.RequiresApi
import androidx.core.app.AlarmManagerCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.base.android.utils.launchAndRepeatWithViewLifecycle
import com.fiz.mono.base.android.utils.setVisible
import com.fiz.mono.base.android.utils.textString
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setupUI()
        setupListeners()
        observeViewStateUpdates()
    }

    private fun init() {
        createNotificationChannel()
        viewModel.start()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun setupUI() {
        binding.apply {
            navigationBarLayout.backButton.setVisible(true)
            navigationBarLayout.actionButton.setVisible(false)
            navigationBarLayout.choiceImageButton.setVisible(false)
            navigationBarLayout.titleTextView.text = getString(R.string.reminder)
        }
    }

    private fun setupListeners() {
        binding.apply {
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
    }

    private fun observeViewStateUpdates() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collect { newState ->
                updateScreenState(newState)
            }
        }
    }

    private fun updateScreenState(newState: ReminderViewState) {
        binding.apply {
            hoursEditText.isEnabled = !newState.isNotifyInstalled
            hoursEditText.error = newState.hoursErrorEditText?.let { getString(it) }
            hoursEditText.textString = newState.timeForReminder.hour

            minutesEditText.isEnabled = !newState.isNotifyInstalled
            minutesEditText.error = newState.minutesErrorEditText?.let { getString(it) }
            minutesEditText.textString = newState.timeForReminder.minute

            setReminderButton.text = getString(newState.textForButtonReminder)
            setReminderButton.isActivated = newState.isNotifyInstalled
            setReminderButton.isEnabled = newState.isCanReminder
        }
    }
}