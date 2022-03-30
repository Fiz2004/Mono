package com.fiz.mono.ui.reminder

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
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentReminderBinding
import com.fiz.mono.receiver.AlarmReceiver
import com.fiz.mono.util.setVisible


class ReminderFragment : Fragment() {
    private val REQUEST_CODE = 0

    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReminderViewModel by viewModels()

    private lateinit var alarmManager: AlarmManager
    private lateinit var notifyIntent: Intent

    private lateinit var alarm: PendingIntent

    private lateinit var notifyPendingIntent: PendingIntent

    var isReminder: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)

        createChannel(
            getString(R.string.mono_notification_channel_id),
            getString(R.string.mono_notification_channel_name)
        )

        alarmManager =
            requireActivity().application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        notifyIntent = Intent(requireActivity().application, AlarmReceiver::class.java)

        alarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                requireActivity().application,
                REQUEST_CODE,
                notifyIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                requireActivity().application,
                REQUEST_CODE,
                notifyIntent,
                PendingIntent.FLAG_NO_CREATE
            )
        }

        notifyPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                requireActivity().application,
                REQUEST_CODE,
                notifyIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                requireActivity().application,
                REQUEST_CODE,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

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

            navigationBarLayout.backButton.setOnClickListener(::backButtonOnClickListener)
            setReminderButton.setOnClickListener(::setReminderButtonOnClickListener)
        }

        viewModel.isCanReminder.observe(viewLifecycleOwner) {
            binding.setReminderButton.isEnabled = it
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                getString(R.string.mono_will_reminder_to_note_transaction_on_this_time_everyday)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun setReminderButtonOnClickListener(view: View?) {
        isReminder = !isReminder
        viewModel.setAlarm(
            isReminder,
            10,
            alarmManager,
            notifyPendingIntent,
            requireActivity()
        )
    }

    private fun backButtonOnClickListener(view: View) {
        findNavController().popBackStack()
    }
}