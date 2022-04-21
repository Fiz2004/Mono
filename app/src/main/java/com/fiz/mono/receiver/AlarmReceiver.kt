package com.fiz.mono.receiver

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import com.fiz.mono.R
import com.fiz.mono.core.ui.reminder.REQUEST_CODE_REMINDER
import com.fiz.mono.core.util.sendNotification


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            context.getText(R.string.mono_will_reminder_to_note_transaction_on_this_time_everyday)
                .toString(),
            context
        )

        val notifyIntent = Intent((context as Application), AlarmReceiver::class.java)

        val notifyPendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_REMINDER,
            notifyIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_IMMUTABLE
            else
                PendingIntent.FLAG_UPDATE_CURRENT
        )


        AlarmManagerCompat.setExactAndAllowWhileIdle(
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            24 * 60 * 60,
            notifyPendingIntent
        )

    }

}