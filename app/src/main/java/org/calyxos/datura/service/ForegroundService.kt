/*
 * SPDX-FileCopyrightText: 2024 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package org.calyxos.datura.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.app.ServiceCompat
import org.calyxos.datura.receiver.PackageReceiver

class ForegroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(PackageReceiver(), IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addDataScheme("package")
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationId = 1
        val channelId = packageName
        val notificationChannel =
            NotificationChannel(
                channelId,
                "Foreground Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            notificationChannel
        )
        val notification = Notification.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("TITLE")
            .setContentText("content")
            .build()
        ServiceCompat.startForeground(
            this,
            notificationId,
            notification,
            1 shl 30
        )
        return super.onStartCommand(intent, flags, startId)
    }
}