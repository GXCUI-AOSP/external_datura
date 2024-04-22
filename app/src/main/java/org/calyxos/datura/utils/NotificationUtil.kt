/*
 * SPDX-FileCopyrightText: 2024 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package org.calyxos.datura.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import org.calyxos.datura.R
import org.calyxos.datura.main.MainActivity
import org.calyxos.datura.models.MinimalApp
import org.calyxos.datura.receiver.DaturaReceiver
import org.calyxos.datura.service.DaturaService
import java.util.UUID

object NotificationUtil {

    const val NOTIFICATION_CHANNEL_ALERT = "NOTIFICATION_CHANNEL_ALERT"

    fun getAlertNotificationChannel(context: Context): NotificationChannel {
        return NotificationChannel(
            NOTIFICATION_CHANNEL_ALERT,
            context.getString(R.string.notification_channel_alert),
            NotificationManager.IMPORTANCE_HIGH
        )
    }

    fun getNewAppNotification(context: Context, app: MinimalApp): Notification {
        val actionIntent = PendingIntent.getBroadcast(
            context,
            UUID.randomUUID().hashCode(),
            Intent(context, DaturaReceiver::class.java).apply {
                action = DaturaService.ACTION_ALLOW_INTERNET_ACCESS
                setPackage(context.packageName)
                putExtra(Intent.EXTRA_PACKAGE_NAME, app.packageName)
                putExtra(Intent.EXTRA_UID, app.uid)
            },
            PendingIntent.FLAG_IMMUTABLE
        )
        val contentIntent = PendingIntent.getActivity(
            context,
            UUID.randomUUID().hashCode(),
            Intent(context, MainActivity::class.java).apply {
                setPackage(context.packageName)
                putExtra(Intent.EXTRA_UID, app.uid)
            },
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ALERT)
            .setSmallIcon(R.drawable.ic_firewall)
            .setLargeIcon(app.icon)
            .setContentTitle(context.getString(R.string.internet_access_denied_title))
            .setContentText(context.getString(R.string.internet_access_denied_desc, app.name))
            .setAutoCancel(true)
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_check,
                    context.getString(R.string.allow),
                    actionIntent
                ).build()
            )
            .setContentIntent(contentIntent)
            .build()
    }
}
