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
import com.fiz.mono.base.android.utils.textString
import com.fiz.mono.feature.reminder.databinding.FragmentReminderBinding
import com.fiz.mono.reminder.utils.cancelNotifications
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val REQUEST_CODE_REMINDER = 0

@AndroidEntryPoint
class ReminderFragment : Fragment() {

    private val viewModel: ReminderViewModel by viewModels()

    private val binding get() = _binding!!
    private var _binding: FragmentReminderBinding? = null

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
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setupListeners()
        observeViewStateUpdates()
        observeViewEffects()
    }

    private fun init() {
        createNotificationChannel()
        viewModel.onEvent(ReminderEvent.ActivityCreated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun setupListeners() {
        binding.apply {
            hoursEditText.doAfterTextChanged {
                viewModel.onEvent(ReminderEvent.HoursEditTextChanged(it.toString()))
            }

            minutesEditText.doAfterTextChanged {
                viewModel.onEvent(ReminderEvent.MinutesEditTextChanged(it.toString()))
            }

            navigationBarLayout.setOnClickListenerBackButton {
                viewModel.onEvent(ReminderEvent.BackButtonClicked)
            }

            setReminderButton.setOnClickListener {
                notificationManager.cancelNotifications()
                if (viewModel.viewState.value.isNotifyInstalled) {
                    alarmManager.cancel(notifyPendingIntent)
                } else {
                    AlarmManagerCompat.setExactAndAllowWhileIdle(
                        alarmManager,
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        viewModel.getTriggerTime(),
                        notifyPendingIntent
                    )
                }
                viewModel.onEvent(ReminderEvent.SetReminderButtonClicked)
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
            hoursEditText.apply {
                isEnabled = !newState.isNotifyInstalled
                error = newState.hoursErrorEditText?.let { getString(it) }
                textString = newState.timeForReminder.hour
            }

            minutesEditText.apply {
                isEnabled = !newState.isNotifyInstalled
                error = newState.minutesErrorEditText?.let { getString(it) }
                textString = newState.timeForReminder.minute
            }

            setReminderButton.apply {
                text = getString(newState.textForButtonReminder)
                isActivated = newState.isNotifyInstalled
                isEnabled = newState.isCanReminder
            }
        }
    }


    private fun observeViewEffects() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewEffects.collect { viewEffect ->
                reactTo(viewEffect)
            }
        }
    }

    private fun reactTo(viewEffect: ReminderViewEffect) {
        when (viewEffect) {
            ReminderViewEffect.MoveReturn -> {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}