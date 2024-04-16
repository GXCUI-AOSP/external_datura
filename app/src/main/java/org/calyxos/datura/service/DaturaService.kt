/*
 * SPDX-FileCopyrightText: 2024 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package org.calyxos.datura.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.NetworkPolicyManager
import android.os.IBinder
import android.os.Process
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import org.calyxos.datura.utils.CommonUtils.PREFERENCE_DEFAULT_INTERNET
import javax.inject.Inject

@AndroidEntryPoint
class DaturaService: Service() {

    @Inject
    lateinit var netPolicyManager: NetworkPolicyManager

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val TAG = DaturaService::class.java.simpleName

    private val packageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent?.action == Intent.ACTION_PACKAGE_ADDED) {
                val uid = intent.getIntExtra(Intent.EXTRA_UID, Process.INVALID_UID)
                val isUpdate = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)

                if (!isUpdate && uid != Process.INVALID_UID) {
                    if (!sharedPreferences.getBoolean(PREFERENCE_DEFAULT_INTERNET, true)) {
                        Log.i(TAG, "Auto-denying internet access for $uid")
                        netPolicyManager.addUidPolicy(uid, NetworkPolicyManager.POLICY_REJECT_ALL)
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
        registerReceiver(packageReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addDataScheme("package")
        })
    }

    override fun onDestroy() {
        unregisterReceiver(packageReceiver)
        super.onDestroy()
    }
}
