package com.fiz.mono.reminder.di

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.reminder.receiver.AlarmReceiver
import com.fiz.mono.reminder.ui.REQUEST_CODE_REMINDER
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ReminderModule {


    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun provideNotificationChannel(@ApplicationContext context: Context): NotificationChannel {
        val notificationChannel = NotificationChannel(
            context.getString(R.string.mono_notification_channel_id),
            context.getString(R.string.mono_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description =
            context.getString(R.string.mono_will_reminder_to_note_transaction_on_this_time_everyday)

        return notificationChannel
    }

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    @Singleton
    fun provideIntent(@ApplicationContext context: Context): Intent {
        return Intent(context, AlarmReceiver::class.java)
    }

    @Provides
    @Singleton
    fun providePendingIntent(@ApplicationContext context: Context, notifyIntent: Intent): PendingIntent {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_IMMUTABLE
        else
            PendingIntent.FLAG_UPDATE_CURRENT

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_REMINDER,
            notifyIntent,
            flags
        )
    }

}