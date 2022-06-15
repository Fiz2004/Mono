package com.fiz.mono.reminder.receiver

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.AlarmManagerCompat
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.reminder.ui.REQUEST_CODE_REMINDER
import com.fiz.mono.reminder.utils.sendNotification
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject

interface GetMainActivity {
    fun get(): Class<*>
}

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {

        notificationManager.sendNotification(
            context.getText(R.string.mono_will_reminder_to_note_transaction_on_this_time_everyday)
                .toString(),
            context,
            ((context.applicationContext as Application) as GetMainActivity).get()
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
            ChronoUnit.DAYS.duration.seconds,
            notifyPendingIntent
        )

    }

}