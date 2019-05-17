package com.isscroberto.dailyprayerandroid.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.isscroberto.dailyprayerandroid.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null) {
            if (intent.action!!.equals(context.getString(R.string.action_prayer_reminder), ignoreCase = true)) {
                NotificationHelper.createSampleDataNotification(context, context.getString(R.string.notification_reminder_title), context.getString(R.string.notification_reminder_message), "", true)
            }
        }
    }

}