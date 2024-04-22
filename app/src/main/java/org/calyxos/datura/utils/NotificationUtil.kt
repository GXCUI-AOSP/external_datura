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
import androidx.navigation.NavDeepLinkBuilder
import org.calyxos.datura.R
import org.calyxos.datura.main.MainActivity
import org.calyxos.datura.models.MinimalApp
import org.calyxos.datura.receiver.DaturaReceiver
import org.calyxos.datura.service.DaturaService
import java.util.UUID

object NotificationUtil {

    private const val NOTIFICATION_CHANNEL_FGS = "NOTIFICATION_CHANNEL_FGS"
    private const val NOTIFICATION_CHANNEL_ALERT = "NOTIFICATION_CHANNEL_ALERT"

    fun getFGSNotification(context: Context): Notification {
        val contentIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.navigation_resource)
            .setDestination(R.id.settingsFragment)
            .setComponentName(MainActivity::class.java)
            .createPendingIntent()

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_FGS)
            .setSmallIcon(R.drawable.ic_firewall)
            .setContentTitle(context.getString(R.string.monitoring_new_app_installs_title))
            .setContentText(context.getString(R.string.monitoring_new_app_installs_desc))
            .setContentIntent(contentIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    fun getNotificationChannels(context: Context): List<NotificationChannel> {
        return listOf(
            NotificationChannel(
                NOTIFICATION_CHANNEL_FGS,
                context.getString(R.string.notification_channel_fgs),
                NotificationManager.IMPORTANCE_LOW
            ),
            NotificationChannel(
                NOTIFICATION_CHANNEL_ALERT,
                context.getString(R.string.notification_channel_alert),
                NotificationManager.IMPORTANCE_HIGH
            )
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
