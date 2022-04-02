package com.fiz.mono.ui.reminder

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentReminderBinding
import com.fiz.mono.receiver.AlarmReceiver
import com.fiz.mono.util.cancelNotifications
import com.fiz.mono.util.setVisible
import java.util.*

val REQUEST_CODE_REMINDER = 0

class ReminderFragment : Fragment() {

    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReminderViewModel by viewModels()

    private lateinit var alarmManager: AlarmManager
    private lateinit var notifyIntent: Intent

    private lateinit var alarm: PendingIntent

    private lateinit var notifyPendingIntent: PendingIntent

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
            (requireActivity().application as Application).getSystemService(Context.ALARM_SERVICE) as AlarmManager
        notifyIntent = Intent((requireActivity().application as Application), AlarmReceiver::class.java)

        alarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                (requireActivity().application as Application),
                REQUEST_CODE_REMINDER,
                notifyIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                (requireActivity().application as Application),
                REQUEST_CODE_REMINDER,
                notifyIntent,
                PendingIntent.FLAG_NO_CREATE
            )
        }

        notifyPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                (requireActivity().application as Application),
                REQUEST_CODE_REMINDER,
                notifyIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                (requireActivity().application as Application),
                REQUEST_CODE_REMINDER,
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

            val notificationManager =
                ContextCompat.getSystemService(
                    requireActivity(),
                    NotificationManager::class.java
                ) as NotificationManager

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
            if (binding.hoursEditText.text.toString() != it.toString())
                binding.hoursEditText.setText(it.toString())
        }
        viewModel.minutes.observe(viewLifecycleOwner) {
            if (binding.minutesEditText.text.toString() != it.toString())
                binding.minutesEditText.setText(it.toString())
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

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
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
        if (viewModel.notify.value == true) {
            viewModel.notify.value = false
            viewModel.cancelNotification(alarmManager, notifyPendingIntent)
            val notificationManager =
                ContextCompat.getSystemService(
                    requireActivity(),
                    NotificationManager::class.java
                ) as NotificationManager
            notificationManager.cancelNotifications()
        } else {

            val currentTime = Calendar.getInstance()
            val needTime = Calendar.getInstance()
            val hours = viewModel.hours.value ?: 0
            val minutes = viewModel.minutes.value ?: 0

            needTime.set(Calendar.HOUR_OF_DAY, hours)
            needTime.set(Calendar.MINUTE, minutes)
            if (currentTime.get(Calendar.HOUR_OF_DAY) > needTime.get(Calendar.HOUR_OF_DAY) &&
                currentTime.get(Calendar.MINUTE) > needTime.get(Calendar.MINUTE)
            )
                needTime.add(Calendar.DATE, -1)

            val differenceTime = (needTime.time.time - currentTime.time.time) / 1000

            viewModel.setAlarm(
                differenceTime.toInt(),
                alarmManager,
                notifyPendingIntent,
                requireActivity()
            )

            viewModel.notify.value = true

            findNavController().popBackStack()
        }
    }

    private fun backButtonOnClickListener(view: View) {
        findNavController().popBackStack()
    }
}