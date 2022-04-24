package com.fiz.mono.reminder.ui

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.util.launchAndRepeatWithViewLifecycle
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.reminder.databinding.FragmentReminderBinding
import com.fiz.mono.reminder.receiver.AlarmReceiver
import com.fiz.mono.reminder.utils.cancelNotifications
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val REQUEST_CODE_REMINDER = 0

@AndroidEntryPoint
class ReminderFragment : Fragment() {

    private val viewModel: ReminderViewModel by viewModels()

    private lateinit var binding: FragmentReminderBinding

    private lateinit var alarmManager: AlarmManager
    private lateinit var notifyIntent: Intent

    private lateinit var alarm: PendingIntent

    private lateinit var notifyPendingIntent: PendingIntent

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationChannel: NotificationChannel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReminderBinding.inflate(inflater, container, false)

        createChannel(
            getString(R.string.mono_will_reminder_to_note_transaction_on_this_time_everyday),
        )

        alarmManager =
            requireActivity().application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        notifyIntent = Intent(context, AlarmReceiver::class.java)

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_IMMUTABLE
        else
            PendingIntent.FLAG_NO_CREATE

        alarm = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_REMINDER,
            notifyIntent,
            flags
        )

        val flagsForNotify = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_IMMUTABLE
        else
            PendingIntent.FLAG_UPDATE_CURRENT

        notifyPendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_REMINDER,
            notifyIntent,
            flagsForNotify
        )

        return binding.root
    }

    private fun createChannel(
        description: String,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = description

            notificationManager.createNotificationChannel(notificationChannel)
        }
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
                if (!viewModel.setHours(it.toString())) {
                    hoursEditText.error = getString(com.fiz.mono.reminder.R.string.invalid_number)
                    viewModel.canReminderNo()
                } else {
                    viewModel.setCanReminder()
                }
            }

            minutesEditText.doAfterTextChanged {
                if (!viewModel.setMinutes(it.toString())) {
                    minutesEditText.error = getString(com.fiz.mono.reminder.R.string.invalid_number)
                    viewModel.canReminderNo()
                } else {
                    viewModel.setCanReminder()
                }
            }

            navigationBarLayout.backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            setReminderButton.setOnClickListener {
                notificationManager.cancelNotifications()

                if (viewModel.uiState.value.isNotifyInstalled) {
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

        subscribe()
    }

    private fun subscribe() {

        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collect { uiState ->
                binding.setReminderButton.isEnabled = uiState.isCanReminder

                if (binding.hoursEditText.text.toString() != uiState.timeForReminder.hour.toString())
                    binding.hoursEditText.setText(uiState.timeForReminder.hour.toString())


                if (binding.minutesEditText.text.toString() != uiState.timeForReminder.minute.toString())
                    binding.minutesEditText.setText(uiState.timeForReminder.minute.toString())

                binding.hoursEditText.isEnabled = !uiState.isNotifyInstalled
                binding.minutesEditText.isEnabled = !uiState.isNotifyInstalled
                binding.setReminderButton.isCheckable = uiState.isNotifyInstalled


                if (uiState.isNotifyInstalled) {
                    binding.setReminderButton.isEnabled = true
                    binding.setReminderButton.text = getString(R.string.remove_reminder)

                    val hours = viewModel.loadHours()
                    val minutes = viewModel.loadMinutes()
                    viewModel.setHours(hours.toString())
                    viewModel.setMinutes(minutes.toString())
                } else {
                    binding.setReminderButton.text = getString(R.string.set_reminder)
                }
            }
        }
    }
}