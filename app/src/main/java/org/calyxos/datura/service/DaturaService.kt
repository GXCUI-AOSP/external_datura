/*
 * SPDX-FileCopyrightText: 2024 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package org.calyxos.datura.service

import android.Manifest
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Process
import android.os.UserHandle
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import lineageos.providers.LineageSettings
import org.calyxos.datura.models.MinimalApp
import org.calyxos.datura.utils.CommonUtils
import org.calyxos.datura.utils.NotificationUtil

@AndroidEntryPoint
class DaturaService : Service() {

    companion object {
        const val ACTION_ALLOW_INTERNET_ACCESS = "org.calyxos.datura.ACTION_ALLOW_INTERNET_ACCESS"
    }

    private lateinit var notificationManager: NotificationManager
    private val TAG = DaturaService::class.java.simpleName

    private val packageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent?.action == Intent.ACTION_PACKAGE_ADDED) {
                val app = getMinimalApp(intent.data!!.encodedSchemeSpecificPart) ?: return
                val isUpdate = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)

                if (!isUpdate && app.requestsInternetPermission) {
                    if (LineageSettings.Secure.getInt(
                            context.contentResolver,
                            LineageSettings.Secure.DEFAULT_RESTRICT_NETWORK_DATA,
                            0
                        ) == 1
                    ) {
                        Log.i(TAG, "Auto-denying internet access for ${app.uid}")
                        notificationManager.notify(
                            app.packageName.hashCode() + UserHandle.getUserId(Process.myUid()),
                            NotificationUtil.getNewAppNotification(context, app)
                        )
                    }
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        notificationManager = getSystemService(NotificationManager::class.java)!!
        notificationManager.createNotificationChannel(
            NotificationUtil.getAlertNotificationChannel(this)
        )

        registerReceiver(
            packageReceiver,
            IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addDataScheme("package")
            }
        )
    }

    override fun onDestroy() {
        unregisterReceiver(packageReceiver)
        super.onDestroy()
    }

    private fun getMinimalApp(packageName: String): MinimalApp? {
        val packageInfo = packageManager.getPackageInfoAsUser(
            packageName,
            PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong()),
            UserHandle.getUserId(Process.myUid())
        )

        if (!Process.isApplicationUid(packageInfo.applicationInfo?.uid ?: Process.INVALID_UID)) {
            Log.i(TAG, "$packageName is not an app")
            return null
        }

        val requestsInternetPerm =
            packageInfo.requestedPermissions?.contains(Manifest.permission.INTERNET) ?: false

        return MinimalApp(
            name = packageInfo.applicationInfo!!.loadLabel(packageManager).toString(),
            packageName = packageInfo.packageName,
            icon = CommonUtils.getIconForPackage(packageManager, packageInfo),
            uid = packageInfo.applicationInfo!!.uid,
            requestsInternetPermission = requestsInternetPerm
        )
    }
}
